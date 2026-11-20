package com.example.coffeeselector.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class LocationDataClass(
    @DocumentId
    val id: String = "",
    val cafeName: String = "",
    val cafeCoordinates: GeoPoint? = null,
    val cafeDescription: String? = null,
    val streetAddress: AddressDataClass? = null,
    val menuStockQuantity: List<Pair<String, Boolean>>? = null,
    val extraAvailability: List<Pair<String, Boolean>>? = null
    )
