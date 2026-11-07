package com.example.coffeeselector.data.repository

import com.google.firebase.database.DatabaseReference
import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await


/**
 * https://firebase.google.com/docs/database/android/read-and-write
 */

class UserRepository {
    private lateinit var database: DatabaseReference


    suspend fun getUser(userId: String): UserDataClass {
        database = Firebase.database.reference
        val userSnapshot = database.child("User").child(userId).get().await()

        return userSnapshot.getValue<UserDataClass>() as UserDataClass
    }

}