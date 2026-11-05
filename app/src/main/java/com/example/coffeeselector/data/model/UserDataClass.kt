package com.example.coffeeselector.data.model


import java.time.LocalDate

data class UserDataClass(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phoneNo: String,
    val address: AddressDataClass,
    val password: String,
    val dateOfBirth: LocalDate,
    val recentOrders: List<OrderDataClass>,
    val unfinishedOrder: OrderDataClass
)
