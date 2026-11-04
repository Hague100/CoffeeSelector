package com.example.coffeeselector.data.model

import java.time.LocalDateTime

data class OrderDataClass(
    val orderId: String,
    val userId: String,
    val customerEmail: String,
    val itemIdQuantityObject: List<Pair<ItemDataClass, Int>>,
    val dateTimeOrderInitiated: LocalDateTime,
    val dateTimeOrderFinished: LocalDateTime,
    val dateTimeOrderCollected: LocalDateTime,
    val totalCost: Double
)
