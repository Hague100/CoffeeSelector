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
    val menuStockQuantity: Map<String, Boolean> = emptyMap(),
    )
