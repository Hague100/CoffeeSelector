package com.example.coffeeselector.data.repository


import com.example.coffeeselector.data.model.OrderDataClass
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class OrderRepository(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val ordersCollection = database.collection("orders")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun createOrderData(orderDC: OrderDataClass): Result<Unit>{
        return try {
            ordersCollection.document().set(orderDC).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readOrderData(docId: String): Result<OrderDataClass>{
        return try {
            val documentSnapshot = ordersCollection.document(docId).get().await()
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

    suspend fun readUsersPastOrders(): Result<List<OrderDataClass>> {
        val uid = getCurrentUserId()
            ?: return Result.failure(Exception("User not logged in"))
        return try{
            val collection = ordersCollection.whereEqualTo("userId", uid).get().await()
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
            ordersCollection.document(docId).update(field, value).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOrderAttribute(docId: String, field: String): Result<Unit> {
        return try {
            ordersCollection.document(docId).update(field, FieldValue.delete()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
