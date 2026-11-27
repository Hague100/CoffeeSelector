package com.example.coffeeselector.ui.auth

import android.util.Patterns
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
        val draftUser = UserDataClass(
            username = username.trim(),
            name = NameDataClass(firstName.trim(), lastName.trim()),
            phoneNo = phoneNo.trim()
        )
        val errors = validateRegistrationInputs(email, password, draftUser)
        if (errors.isNotEmpty()) {
            report(false, errors.joinToString("\n"))
            return
        }
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
    private fun validateRegistrationInputs(
        email: String,
        password: String,
        userData: UserDataClass
    ): List<String> {
        val errors = mutableListOf<String>()

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errors.add("Please enter a valid email address.")
        }
        if (!passwordRegex.matches(password)) {
            errors.add("Password must be at 6 to 30 characters " +
                    "and include at least one uppercase and one lowercase letter.")
        }
        errors.addAll(userData.validate())
        return errors
    }



    fun login(email: String, password: String, report: (Boolean, String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            report(false, "Email and password cannot be blank.")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                report(task.isSuccessful,
                    if (task.isSuccessful) "Signed in" else "Sign-in failed")
            }
    }
    fun logout() = auth.signOut()
    companion object {
        private val passwordRegex = Regex("""^(?=.*[A-Z])(?=.*[a-z]).{6,30}$""")
    }
}