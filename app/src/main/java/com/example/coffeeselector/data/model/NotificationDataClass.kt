package com.example.coffeeselector.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Locale
data class NotificationDataClass(
    @DocumentId
    val id: String = "",
    @get:ServerTimestamp
    val sentAt: Timestamp? = null,
    val message: String = "",
    val isRead: Boolean = false
) {
    @get:Exclude
    val formattedSentAt: String
        get() = sentAt?.toDate()?.let { date ->
            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
        } ?: "Sending..."
}