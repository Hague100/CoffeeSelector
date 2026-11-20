package com.example.coffeeselector.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.Locale

data class ItemDataClass(
    @DocumentId
    val id: String = "",
    val itemName: String = "",
    val itemPrice: Double = 0.0,
    val itemDescription: String = ""
) {
    @get:Exclude
    val formattedPrice: String
        get() = String.format(Locale.UK, "£%.2f", itemPrice)

    fun formattedPriceMulti(quantity: Int): String {
        return String.format(Locale.UK, "£%.2f", itemPrice * quantity)
    }
}