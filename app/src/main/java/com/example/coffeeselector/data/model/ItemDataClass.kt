package com.example.coffeeselector.data.model

data class ItemDataClass(
    val itemId: String,
    val itemName: String,
    val ingredientList: List<String>,
    val environmentalAllergens: List<String>,
    val dailyPercentageList: List<Double>,
    val perServingList: List<Double>,
    val perOrderLimit: Int,
    val customizerMaxObject: List<Pair<ExtraDataClass, Int>>,
    val itemPrice: Double
)
