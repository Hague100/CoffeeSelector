package com.example.coffeeselector.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class NavigationViewModel(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    val unreadNotificationCount: StateFlow<Int> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(0)
            awaitClose {}
            return@callbackFlow
        }

        val registration = database.collection("users")
            .document(uid)
            .collection("notifications")
            .whereEqualTo("read", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }

        awaitClose { registration.remove() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    companion object {
        fun provideFactory(
            database: FirebaseFirestore,
            auth: FirebaseAuth
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NavigationViewModel(database, auth) as T
            }
        }
    }
}