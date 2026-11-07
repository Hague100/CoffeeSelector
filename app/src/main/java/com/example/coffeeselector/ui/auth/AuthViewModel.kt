package com.example.coffeeselector.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    fun register(email: String, password: String, report: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                report(task.isSuccessful,
                    if (task.isSuccessful) "Registered" else (task.exception?.localizedMessage ?: "Registration failed"))
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