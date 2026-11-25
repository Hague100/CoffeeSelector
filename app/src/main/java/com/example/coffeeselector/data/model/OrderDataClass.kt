package com.example.coffeeselector.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class OrderDataClass(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val locationId: String = "",
    val customerEmail: String = "",
    val itemIdQuantityObject: Map<String, Int> = emptyMap(),
    val dateTimeOrderInitiated: Date? = null,
    val dateTimeOrderFinished: Date? = null,
    val dateTimeOrderCollected: Date? = null,
    val totalCost: Double = 0.0,
    val canceled: Boolean = false,
    val rating: Int? = null
) {
    @get:Exclude
    val formattedCost: String
        get() = String.format(Locale.UK, "£%.2f", totalCost)
    @get:Exclude
    val collectedLocalDateTime: LocalDateTime?
        get() = dateTimeOrderCollected?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()

    @get:Exclude
    val formattedCollectedTime: String?
        get() = collectedLocalDateTime?.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.UK)
        )
    @get:Exclude
    val statusMessage: String
        get() = when {
            canceled -> "Canceled"
            dateTimeOrderCollected != null -> "Completed"
            dateTimeOrderFinished != null -> "Ready To Collect"
            dateTimeOrderInitiated != null -> "Processing"
            else -> "Incomplete"
        }
}



