package com.example.coffeeselector.data.model


import java.time.LocalDate

data class UserDataClass(
    val userId: String,
    val name: NameDataClass,
    val phoneNo: String,
    val address: AddressDataClass,
    val dateOfBirth: LocalDate,
    val recentOrders: List<OrderDataClass>,
    val unfinishedOrder: OrderDataClass
)
