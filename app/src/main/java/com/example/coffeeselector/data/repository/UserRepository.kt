package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


/**
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin
 * https://firebase.google.com/docs/rules/basics
 * https://firebase.google.com/docs/firestore/query-data/get-data
 */

class UserRepository(
    database: FirebaseFirestore,
    private val auth: FirebaseAuth) {
    private val usersCollection = database.collection("users")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    suspend fun createUser(userDC: UserDataClass): Result<Unit>{
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            usersCollection.document(uid).set(userDC).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun readUser(): Result<UserDataClass> {
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            val documentSnapshot = usersCollection.document(uid).get().await()
            val user = documentSnapshot.toObject<UserDataClass>()
            if (documentSnapshot.exists() && user != null) {
                Result.success(user)
            } else Result.failure(NoSuchElementException("No user found"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateUserAttribute(field: String, value: Any?): Result<Unit> {
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            usersCollection.document(uid).update(field, value).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteUserAttribute(field: String): Result<Unit> {
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            usersCollection.document(uid).update(field, FieldValue.delete()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
/**
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

 * Decided after some issues to try an await function which requires a Coroutines
 * https://developer.android.com/topic/libraries/architecture/coroutines
 * https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-triple/

    suspend fun readUserData(userDocId: String): Triple<Boolean, String, UserDataClass?> {
        val onResult:  Triple<Boolean, String, UserDataClass?> = try{
            val document = database.collection("users").document(userDocId).get().await()
            if (document.exists()) {
                val userData = document.toObject<UserDataClass>()
                if (userData != null) Triple(true, "User data", userData)
                else Triple(false, "Failed to read data", null)
            } else Triple(false, "No user data", null)
        } catch (e: Exception){
            Triple(false, "User data not found", null)
        }
        return onResult
    }

 */