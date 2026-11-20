package com.example.coffeeselector.ui.auth

import androidx.lifecycle.ViewModel
import com.example.coffeeselector.data.model.NameDataClass
import com.example.coffeeselector.data.model.UserDataClass
import com.example.coffeeselector.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    fun register(email: String, password: String, report: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                report(task.isSuccessful,
                    if (task.isSuccessful) "Registered" else (task.exception?.localizedMessage ?: "Registration failed"))
            }
    }

    fun registerUserData(email: String, password: String, firstName: String, lastName: String, username: String, phoneNo: String, report: (Boolean, String) -> Unit) {
        register(email, password) { isSuccess, authMessage ->
            if (isSuccess) {
                val userDocId = auth.currentUser?.uid
                val userData = UserDataClass(userDocId.toString(), username, NameDataClass(firstName, lastName), phoneNo)
                if (userDocId != null) {
                    userRepository.createUserData(userData) { isDocSuccess, docMessage ->
                        if (isDocSuccess) {
                            report(true, "Registration completely successfully.")
                        } else {
                            report(false, docMessage)
                        }
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