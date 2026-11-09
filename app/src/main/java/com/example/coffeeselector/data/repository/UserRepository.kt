package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


/**
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin
 * https://firebase.google.com/docs/rules/basics
 */

class UserRepository {
    private val database = Firebase.firestore


    fun newUserData(userData: UserDataClass, userDocId: String, onResult: (Boolean, String) -> Unit) {
        database.collection("users").document(userDocId).set(userData)
            .addOnSuccessListener { onResult(true, "Data Added") }
            .addOnFailureListener { onResult(false, "Data not added") }
    }

}