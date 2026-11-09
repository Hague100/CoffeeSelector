package com.example.coffeeselector.data.model


import java.time.LocalDate

data class UserDataClass(
    val username: String,
    val name: NameDataClass,
    val phoneNo: String ,
    val address: AddressDataClass? = null,
    val dateOfBirth: LocalDate? = null,
    val recentOrders: List<OrderDataClass>? = null,
    val unfinishedOrder: OrderDataClass? = null
)
