package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


/**
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin
 * https://firebase.google.com/docs/rules/basics
 * https://firebase.google.com/docs/firestore/query-data/get-data
 */

class UserRepository {
    private val database = Firebase.firestore
    fun createUserData(userData: UserDataClass, onResult: (Boolean, String) -> Unit) {
        if (userData.id.isBlank()) {
            onResult(false, "User ID cannot be empty")
            return
        }
        database.collection("users").document().set(userData)
            .addOnSuccessListener { onResult(true, "Data added") }
            .addOnFailureListener { onResult(false, "Data not added") }
    }

//    fun readUserData(userDocId: String, onResult: (Boolean, String, UserDataClass?) -> Unit) {
//        database.collection("users").document(userDocId).get()
//            .addOnSuccessListener { document ->
//                if (document != null && document.exists()) {
//                    val userData = document.toObject<UserDataClass>()
//                    if (userData != null) onResult(true, "User data", userData)
//                    else onResult(false, "Failed to read data", null)
//                } else onResult(false, "No user data", null)
//            }
//            .addOnFailureListener { exception ->
//                onResult(false, "User data not found", null) }
//    }

    /**
     * Decided after some issues to try an await function which requires a Coroutines
     * https://developer.android.com/topic/libraries/architecture/coroutines
     * https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-triple/
      */
//    suspend fun readUserData(userDocId: String): Triple<Boolean, String, UserDataClass?> {
//        val onResult:  Triple<Boolean, String, UserDataClass?> = try{
//            val document = database.collection("users").document(userDocId).get().await()
//            if (document.exists()) {
//                val userData = document.toObject<UserDataClass>()
//                if (userData != null) Triple(true, "User data", userData)
//                else Triple(false, "Failed to read data", null)
//            } else Triple(false, "No user data", null)
//        } catch (e: Exception){
//            Triple(false, "User data not found", null)
//        }
//        return onResult
//    }
    suspend fun readUserData(userDocId: String): Result<UserDataClass> {
        return try {
            val document = database.collection("users").document(userDocId).get().await()
            if (document.exists()) {
                val userData = document.toObject<UserDataClass>()
                if (userData != null) Result.success(userData)
                else Result.failure(IllegalStateException("Failed to read data"))
            } else Result.failure(NoSuchElementException("No user data"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserAttribute(docId: String, field: String, value: Any?): Result<Unit> {
        return try {
            database.collection("users").document(docId).update(field, value).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUserAttribute(docId: String, field: String): Result<Unit> {
        return try {
            database.collection("users").document(docId).update(field, FieldValue.delete()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}