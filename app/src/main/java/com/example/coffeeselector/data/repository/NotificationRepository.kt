package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.NotificationDataClass
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class NotificationRepository(
    private val database: FirebaseFirestore
) {


    suspend fun createNotification(notificationDC: NotificationDataClass): Result<Unit>{
        return try {
            database.collection("notifications").document().set(notificationDC).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun readNotification(docId: String): Result<NotificationDataClass> {
        return try {
            val documentSnapshot = database.collection("notifications").document(docId).get().await()
            val notification = documentSnapshot.toObject<NotificationDataClass>()

            if (documentSnapshot.exists() && notification != null) {
                Result.success(notification)
            } else Result.failure(NoSuchElementException("No Notification Found"))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}