package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


/**
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin
 * https://firebase.google.com/docs/rules/basics
 * https://firebase.google.com/docs/firestore/query-data/get-data
 */

class UserRepository {
    private val database = Firebase.firestore
    fun createUserData(userData: UserDataClass, userDocId: String, onResult: (Boolean, String) -> Unit) {
        database.collection("users").document(userDocId).set(userData)
            .addOnSuccessListener { onResult(true, "Data added") }
            .addOnFailureListener { onResult(false, "Data not added") }
    }

    fun readUserData(userDocId: String, onResult: (Boolean, String, UserDataClass?) -> Unit) {
        database.collection("users").document(userDocId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userData = document.toObject<UserDataClass>()
                    if (userData != null) onResult(true, "User data", userData)
                    else onResult(false, "Failed to read data", null)
                } else onResult(false, "No user data", null)
            }
            .addOnFailureListener { exception ->
                onResult(false, "User data not found", null) }
    }



}