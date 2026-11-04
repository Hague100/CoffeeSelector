package com.example.coffeeselector.data.model

data class ExtraDataClass(
    val extraId: String,
    val extraDescription: String,
    val extraName: String,
    val extraPrice: Double,
    val extraIngredientList: List<String>,
    val extraEnvironmentalAllergens: List<String>,
    val perExtraList: List<Double>
    )
