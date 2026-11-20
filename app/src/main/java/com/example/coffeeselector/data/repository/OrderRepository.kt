package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.OrderDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class OrderRepository {
    private val database = Firebase.firestore

    suspend fun createOrderData(orderData: OrderDataClass): Result<Unit>{
        return try {
            database.collection("orders").document().set(orderData).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
