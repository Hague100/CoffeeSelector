package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.UserDataClass
import com.example.coffeeselector.data.model.ValidationException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


/**
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://firebase.google.com/docs/firestore/manage-data/add-data#kotlin
 * https://firebase.google.com/docs/rules/basics
 * https://firebase.google.com/docs/firestore/query-data/get-data
 * Decided after some issues to try an await function which requires a Coroutines
 * https://developer.android.com/topic/libraries/architecture/coroutines
 * https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-triple/
 */

class UserRepository(
    database: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val usersCollection = database.collection("users")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    suspend fun createUser(userDC: UserDataClass): Result<Unit>{
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
        val errors = userDC.validate()
        if (errors.isNotEmpty()) return Result.failure(ValidationException(errors))
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
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
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
}
