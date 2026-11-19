package com.example.coffeeselector.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

data class OrderDataClass(
    val orderId: String = "",
    val userId: String = "",
    val locationId: String = "",
    val customerEmail: String = "",
    val itemIdQuantityObject: List<Pair<String, Int>> = emptyList(),
    @ServerTimestamp
    val dateTimeOrderInitiated: Date? = null,
    val dateTimeOrderFinished: Date? = null,
    val dateTimeOrderCollected: Date? = null,
    val totalCost: Double = 0.0
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
}

fun Date.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return this.toInstant()
        .atZone(zoneId)
        .toLocalDateTime()
}

