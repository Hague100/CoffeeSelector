package com.example.coffeeselector.ui.profile

import androidx.lifecycle.ViewModel
import com.example.coffeeselector.data.model.UserDataClass
import com.example.coffeeselector.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 *https://developer.android.com/topic/libraries/architecture/viewmodel
 * was using localDate but ran into problems with firebase natively reading it
 */
class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore
    private val userRepository = UserRepository(database,auth)
    private val userDocId = auth.currentUser?.uid
    private val _userState = MutableStateFlow<UserDataClass?>(null)
    val userData: StateFlow<UserDataClass?> get() = _userState.asStateFlow()

    /**
     * guide to passing mutable status messages
     * https://developers.arcgis.com/kotlin/programming-patterns/manage-geoview-state/#viewmodel-pattern
     * mutable state flow
     * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-mutable-state-flow/
     */
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> get() = _statusMessage.asStateFlow()

    /**
     * guide on using coroutines in viewModel scope
     * https://developer.android.com/kotlin/coroutines#use-coroutines-for-main-safety
     */
    init {
        getUserData()
    }


    fun getUserData(){
//        val currentUserDocId = userDocId
//        if (currentUserDocId != null){
//            viewModelScope.launch {
//                val (isSuccess, message, userDC) = userRepository.readUser()
//                if(isSuccess && userDC != null){
//                    _userState.value = userDC as UserDataClass?
//                }
//                _statusMessage.value = message
//            }
//        } else _statusMessage.value = "No user logged in"
    }
}

//    fun getUserDataClass(onResult: (Boolean, String) -> Unit) {
//        if (userDocId != null){
//            userRepository.readUserData(userDocId) {
//                    isSuccess, message, userDC ->
//                if(isSuccess && userDC != null){
//                    _userState.value = userDC
//                }
//                onResult(isSuccess, message)
//            }
//        } else {
//            onResult(false, "No user logged in")
//        }
//    }



