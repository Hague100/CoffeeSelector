package com.example.coffeeselector.ui

import kotlinx.serialization.Serializable


@Serializable object LocationListRoute
@Serializable
data class MenuRoute(
    val locationId: String,
    val itemIds: List<String>,
    val itemAvailabilities: List<Boolean>
)
@Serializable object ProfileRoute
@Serializable object LoginRoute
@Serializable object RegisterRoute
@Serializable object RecentOrdersRoute
@Serializable object OrderListRoute
@Serializable
data class OrderOverviewRoute(
    val orderId: String
)
@Serializable object NotificationsRoute
