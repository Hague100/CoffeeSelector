package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.ItemDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class ItemRepository {

    private val database = Firebase.firestore
    /**
     * https://rockthejvm.com/articles/functional-error-handling-in-kotlin-part-2-result-and-either
     * Method	What it does
     * Result.success(Unit)	Signals a void/write operation succeeded.
     * Result.success(data)	Signals a read operation succeeded and attaches data.
     * Result.failure(e)	Captures an exception without crashing the app.
     * .fold(onSuccess, onFailure)	Handles both branches and returns a single result.
     * .isSuccess / .isFailure	Boolean checks if you prefer standard if/else statements.
     *
     */
//    suspend fun readItem(docId: String): Triple<Boolean, String, ItemDataClass?> {
//        val onResult: Triple<Boolean, String, ItemDataClass?> = try {
//            val documentSnapshot = database.collection("items").document(docId).get().await()
//            val item = documentSnapshot.toObject<ItemDataClass>()
//
//            if(documentSnapshot.exists() && item != null) Triple(true, "Data fetched successfully", item)
//            else Triple(false, "No Item Found", null)
//        }catch (e: CancellationException) {
//            throw e
//        } catch (e: Exception) {
//            Triple(false, "Error Occurred", null)
//        }
//        return onResult
//    }
    suspend fun readItem(docId: String): Result<ItemDataClass> {
        return try {
            val documentSnapshot = database.collection("items").document(docId).get().await()
            val item = documentSnapshot.toObject<ItemDataClass>()
            if (documentSnapshot.exists() && item != null) Result.success(item)
            else Result.failure(NoSuchElementException("No Item Found"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}