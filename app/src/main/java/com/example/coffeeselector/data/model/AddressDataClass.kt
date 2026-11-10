package com.example.coffeeselector.data.model

data class AddressDataClass(
    val line1: String = "",
    val line2: String? = null,
    val city: String = "",
    val county: String? = null,
    val postCode: String = ""
)
