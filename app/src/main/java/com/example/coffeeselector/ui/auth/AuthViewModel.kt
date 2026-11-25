package com.example.coffeeselector.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.NameDataClass
import com.example.coffeeselector.data.model.UserDataClass
import com.example.coffeeselector.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore
    private val userRepository = UserRepository(database, auth)
    fun register(email: String, password: String, report: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                report(
                    task.isSuccessful,
                    if (task.isSuccessful) "Registered" else (task.exception?.localizedMessage
                        ?: "Registration failed")
                )
            }
    }

    fun registerUserData(
        email: String, password: String, firstName: String, lastName: String, username: String,
        phoneNo: String, report: (Boolean, String) -> Unit
    ) {
        register(email, password) { isSuccess, authMessage ->
            if (isSuccess) {
                val userDocId = auth.currentUser?.uid
                if (userDocId != null) {
                    val userData = UserDataClass(
                        userDocId,
                        username,
                        NameDataClass(firstName, lastName),
                        phoneNo
                    )
                    viewModelScope.launch {
                        val result = userRepository.createUser(userData)
                        result.onSuccess {report( true, "Registration completed Successfully")}
                            .onFailure {exception ->   report(false, exception.message ?: "Failed to save user data.")}
                    }
                } else {
                    report(false, "User authenticated but no ID found.")
                }
            } else {
                report(false, authMessage)
            }
        }
    }


    fun login(email: String, password: String, report: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                report(task.isSuccessful,
                    if (task.isSuccessful) "Signed in" else "Sign-in failed")
            }
    }
    fun logout() = auth.signOut()
}