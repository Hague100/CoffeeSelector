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
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not logged in"))
        return try {
            val orderDoc = ordersCollection.document()
            val orderWithUser = orderDC.copy(userId = uid)
            database.runBatch { batch ->
                batch.set(orderDoc, orderWithUser)
                if (orderWithUser.dateTimeOrderInitiated == null) {
                    batch.update(usersCollection.document(uid), "unfinishedOrder", orderDoc.id)
                }
            }.await()
            Result.success(orderDoc.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun initiateOrder(docId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not logged in"))
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
            if (documentSnapshot.exists() && order != null) Result.success(order)
            else Result.failure(NoSuchElementException("No order found"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readUsersPastOrders(): Result<List<OrderDataClass>> {
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
        return try {
            val collection = ordersCollection.whereEqualTo("userId", uid)
                .get().await()
            val orderList = collection.documents.mapNotNull { it.toObject<OrderDataClass>() }
                .filter { it.dateTimeOrderInitiated != null }
            if (orderList.isNotEmpty()) Result.success(orderList)
            else Result.failure(NoSuchElementException("No orders found"))
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

    suspend fun deleteUninitiatedOrder(docId: String): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not logged in"))
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

    suspend fun updateOrderItemsAndTotalCost(docId: String, items: Map<String, Int>, totalCost: Double): Result<Unit> {
        return try {
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
    suspend fun updateOrderAsFinished(docId: String): Result<Date> = try {
        val now = Date()
        ordersCollection.document(docId).update("dateTimeOrderFinished", now).await()
        Result.success(now)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateOrderAsCollected(docId: String): Result<Date> = try {
        val now = Date()
        ordersCollection.document(docId).update("dateTimeOrderCollected", now).await()
        Result.success(now)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun removeFromRecentOrders(orderIds: List<String>): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not logged in"))
        if (orderIds.isEmpty()) return Result.success(Unit)
        return try {
            usersCollection.document(uid)
                .update("recentOrders", FieldValue.arrayRemove(*orderIds.toTypedArray()))
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
