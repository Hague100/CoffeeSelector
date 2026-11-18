package com.example.coffeeselector.data.model

data class LocationDataClass(
    val cafeName: String = "",
    val cafeCoordinatesObject: Pair<Double, Double>? = null,
    val cafeDescription: String? = null,
    val streetAddress: AddressDataClass? = null,
    val menuStockQuantity: List<Pair<String, Boolean>>? = null,
    val extraAvailability: List<Pair<String, Boolean>>? = null
    )
