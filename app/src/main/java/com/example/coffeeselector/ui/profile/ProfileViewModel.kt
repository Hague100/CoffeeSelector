package com.example.coffeeselector.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.UserDataClass
import com.example.coffeeselector.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * https://developer.android.com/topic/libraries/architecture/viewmodel
 * was using localDate but ran into problems with firebase natively reading it
 * guide to passing mutable status messages
 * https://developers.arcgis.com/kotlin/programming-patterns/manage-geoview-state/#viewmodel-pattern
 * mutable state flow
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-mutable-state-flow/
 * guide on using coroutines in viewModel scope
 * https://developer.android.com/kotlin/coroutines#use-coroutines-for-main-safety
 */
class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore
    private val userRepository = UserRepository(database, auth)
    private val _userState = MutableStateFlow<UserDataClass?>(null)
    val userData: StateFlow<UserDataClass?> get() = _userState.asStateFlow()
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> get() = _statusMessage.asStateFlow()


    init { getUserData() }

    fun getUserData() {
        viewModelScope.launch {
            userRepository.readUser()
                .onSuccess { user -> _userState.value = user }
                .onFailure { exception -> _statusMessage.value = exception.message
                    ?: "Failed to load user profile"
                }
        }
    }
}



