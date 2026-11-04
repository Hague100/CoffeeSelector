package com.example.coffeeselector.data.model

data class LocationDataClass(
    val cafeId: String,
    val cafeName: String,
    val cafeCoordinatesObject: Pair<Double, Double>,
    val cafeDescription: String,
    val streetAddress: addressDataClass,
    val menuStockQuantity: List<Pair<ItemDataClass, Int>>,
    val extraAvailability: List<Pair<ExtraDataClass, Boolean>>
    )
