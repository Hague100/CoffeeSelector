package com.example.coffeeselector.data.repository


import com.example.coffeeselector.data.model.OrderDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class OrderRepository {
    private val database = Firebase.firestore

    suspend fun createOrderData(orderDC: OrderDataClass): Result<Unit>{
        return try {
            database.collection("orders").document().set(orderDC).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readOrderData(docId: String): Result<OrderDataClass>{
        return try {
            val documentSnapshot = database.collection("orders").document(docId).get().await()
            val order = documentSnapshot.toObject<OrderDataClass>()
            if (documentSnapshot.exists() && order != null){
                Result.success(order)
            } else Result.failure(NoSuchElementException("No order found"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readUsersPastOrders(userId: String): Result<List<OrderDataClass>> {
        return try{
            val collection = database.collection("orders").whereEqualTo("userId", userId).get().await()
            if (!collection.isEmpty) {
                val orderList = collection.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<OrderDataClass>()
                }
                Result.success(orderList)
            } else Result.failure(NoSuchElementException("No orders found"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderAttribute(docId: String, field: String, value: Any?): Result<Unit> {
        return try {
            database.collection("orders").document(docId).update(field, value).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOrderAttribute(docId: String, field: String): Result<Unit> {
        return try {
            database.collection("orders").document(docId).update(field, FieldValue.delete()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
