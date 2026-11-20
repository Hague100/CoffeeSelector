package com.example.coffeeselector.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
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
    @ServerTimestamp
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
    val initiatedLocalDateTime: LocalDateTime?
        get() = dateTimeOrderInitiated?.toLocalDateTime()

    @get:Exclude
    val finishedLocalDateTime: LocalDateTime?
        get() = dateTimeOrderFinished?.toLocalDateTime()

    @get:Exclude
    val collectedLocalDateTime: LocalDateTime?
        get() = dateTimeOrderCollected?.toLocalDateTime()

    @get:Exclude
    val formattedCollectedTime: String?
        get() = collectedLocalDateTime?.format(
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.UK)
        )

    fun Date.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
        return this.toInstant()
            .atZone(zoneId)
            .toLocalDateTime()
    }
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



