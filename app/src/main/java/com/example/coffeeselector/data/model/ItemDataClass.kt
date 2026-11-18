package com.example.coffeeselector.data.model

import java.util.Locale

data class ItemDataClass(
    val itemName: String,
    val itemPrice: Double,
    val itemDescription: String,
    val customizerMaxObject: List<Pair<ExtraDataClass, Int>>? = null
) {

    val formattedPrice: String
        get() = String.format(Locale.UK, "£%.2f", itemPrice)
}