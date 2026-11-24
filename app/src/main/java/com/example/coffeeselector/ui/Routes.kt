package com.example.coffeeselector.ui

import kotlinx.serialization.Serializable


@Serializable object LocationListRoute
@Serializable
data class MenuRoute(
    val locationId: String,
    val itemIds: List<String>,
    val itemAvailabilities: List<Boolean>
)
