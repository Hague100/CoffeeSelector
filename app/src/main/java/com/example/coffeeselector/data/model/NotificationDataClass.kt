package com.example.coffeeselector.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
    @DocumentId
    val id: String = "",
    @get:ServerTimestamp
    val sentAt: Timestamp? = null,
    val message: String = "",
    val isRead: Boolean = false
)