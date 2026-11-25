package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.NotificationDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class NotificationRepository(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val notificationsCollection = database.collection("notifications")
    private val usersCollection = database.collection("users")
    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun createNotificationForUser(notificationDC: NotificationDataClass): Result<String> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            val notificationDoc = notificationsCollection.document()
            val notificationWithUser = notificationDC.copy(userId = uid)
            database.runBatch { batch ->
                batch.set(notificationDoc, notificationWithUser)
                batch.update(
                    usersCollection.document(uid),
                    "notificationsId",
                    FieldValue.arrayUnion(notificationDoc.id)
                )
            }.await()
            Result.success(notificationDoc.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun markNotificationsAsRead(docIdList: List<String>): Result<Unit> {
        if (docIdList.isEmpty()) return Result.success(Unit)
        return try {
            database.runBatch { batch ->
                docIdList.forEach { id -> batch.update(notificationsCollection.document(id), "isRead", true) }
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllNotifications(docIdList: List<String>): Result<Unit> {
        val uid = getCurrentUserId() ?: return Result.failure(IllegalStateException("User not authenticated"))
        return try {
            database.runBatch { batch ->
                docIdList.forEach { id -> batch.delete(notificationsCollection.document(id)) }
                batch.update(usersCollection.document(uid), "notificationsId", emptyList<String>())
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}