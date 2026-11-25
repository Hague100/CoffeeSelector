package com.example.coffeeselector.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.NotificationDataClass
import com.example.coffeeselector.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationDataClass>>(emptyList())
    val notifications: StateFlow<List<NotificationDataClass>> = _notifications.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()
    private val _newNotificationEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val newNotificationEvent: SharedFlow<String> = _newNotificationEvent.asSharedFlow()
    private var knownNotificationIds: Set<String>? = null
    private var userListenerRegistration: ListenerRegistration? = null
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val uid = firebaseAuth.currentUser?.uid
        userListenerRegistration?.remove()
        knownNotificationIds = null

        if (uid != null) observeUserNotifications(uid)
        else _notifications.value = emptyList()
    }
    val unreadNotificationCount: StateFlow<Int> = _notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    init { auth.addAuthStateListener(authStateListener) }

    private fun observeUserNotifications(uid: String) {
        _isLoading.value = true
        userListenerRegistration = database.collection("notifications")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                val loaded = snapshot.documents.mapNotNull { it.toObject<NotificationDataClass>() }
                    .sortedByDescending { it.sentAt?.toDate() }

                val previousIds = knownNotificationIds
                val currentIds = loaded.map { it.id }.toSet()

                if (previousIds == null) knownNotificationIds = currentIds
                else {
                    val newestNotification = loaded.firstOrNull { it.id !in previousIds }
                    knownNotificationIds = currentIds

                    if (newestNotification != null) {
                        _newNotificationEvent.tryEmit(newestNotification.message.ifBlank { "You have a new notification" })
                    }
                }
                _notifications.value = loaded
                _isLoading.value = false
            }
    }

    fun markAllAsRead() {
        val unreadIds = _notifications.value.filter { !it.isRead }.map { it.id }
        if (unreadIds.isEmpty()) return

        viewModelScope.launch {
            notificationRepository.markNotificationsAsRead(unreadIds)
                .onSuccess { _notifications.value = _notifications.value.map { it.copy(isRead = true) } }
        }
    }

    fun clearAllNotifications() {
        val allIds = _notifications.value.map { it.id }
        if (allIds.isEmpty()) return

        viewModelScope.launch {
            notificationRepository.clearAllNotifications(allIds)
                .onSuccess { _notifications.value = emptyList() }
                .onFailure { e -> _statusMessage.value = e.message ?: "Failed to clear notifications" }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authStateListener)
        userListenerRegistration?.remove()
    }

    companion object {
        fun provideFactory(
            database: FirebaseFirestore,
            auth: FirebaseAuth,
            notificationRepository: NotificationRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationViewModel(database, auth, notificationRepository) as T
            }
        }
    }
}