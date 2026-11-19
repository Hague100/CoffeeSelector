package com.example.coffeeselector.data.model

import com.google.firebase.firestore.Exclude
import java.util.Locale

data class ItemDataClass(
    val itemName: String,
    val itemPrice: Double,
    val itemDescription: String,
    val customizerMaxObject: List<Pair<String, Int>>? = null
) {
    @get:Exclude
    val formattedPrice: String
        get() = String.format(Locale.UK, "£%.2f", itemPrice)

    fun formattedPriceMulti(quantity: Int): String {
        return String.format(Locale.UK, "£%.2f", itemPrice * quantity)
    }
}