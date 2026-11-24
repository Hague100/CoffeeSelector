package com.example.coffeeselector.data.repository


import com.example.coffeeselector.data.model.OrderDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException

class OrderRepository(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val ordersCollection = database.collection("orders")
    private val usersCollection = database.collection("users")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun createOrderData(orderDC: OrderDataClass): Result<String> {
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))
        return try {
            val docRef = ordersCollection.document()
            val orderWithUser = orderDC.copy(userId = uid)
            database.runBatch { batch ->
                batch.set(docRef, orderWithUser)
                if (orderWithUser.dateTimeOrderInitiated == null) {
                    batch.update(usersCollection.document(uid), "unfinishedOrder", docRef.id)
                }
            }.await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitOrder(docId: String): Result<Unit> {
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))

        return try {
            database.runBatch { batch ->
                batch.update(ordersCollection.document(docId), "dateTimeOrderInitiated", Date())
                batch.update(
                    usersCollection.document(uid),
                    mapOf(
                        "unfinishedOrder" to null,
                        "recentOrders" to FieldValue.arrayUnion(docId)
                    )
                )
            }.await()
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
            val collection = ordersCollection.whereEqualTo("userId", uid)
                .whereNotEqualTo("dateTimeOrderInitiated", null).get().await()
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

    suspend fun deleteDraftOrder(docId: String): Result<Unit> {
        val uid = getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User not logged in"))

        return try {
            database.runBatch { batch ->
                batch.delete(ordersCollection.document(docId))
                batch.update(usersCollection.document(uid), "unfinishedOrder", null)
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderItemsAndCost(
        docId: String,
        items: Map<String, Int>,
        totalCost: Double
    ): Result<Unit> = try {
        ordersCollection.document(docId).update(
            mapOf(
                "itemIdQuantityObject" to items,
                "totalCost" to totalCost
            )
        ).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

}
