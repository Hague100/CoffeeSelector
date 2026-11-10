package com.example.coffeeselector.ui.profile

import androidx.lifecycle.ViewModel
import com.example.coffeeselector.data.model.UserDataClass
import com.example.coffeeselector.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
/**
 *https://developer.android.com/topic/libraries/architecture/viewmodel
 * was using localDate but ran into problems with firebase natively reading it
 */
class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val userDocId = auth.currentUser?.uid
    private val _userState = MutableStateFlow(UserDataClass())
    val userData: UserDataClass
        get() = _userState.value

    fun getUserDataClass(onResult: (Boolean, String) -> Unit) {
        if (userDocId != null){
            userRepository.readUserData(userDocId) {
                    isSuccess, message, userDC ->
                if(isSuccess && userDC != null){
                    _userState.value = userDC
                }
                onResult(isSuccess, message)
            }
        } else {
            onResult(false, "No user logged in")
        }
    }

}