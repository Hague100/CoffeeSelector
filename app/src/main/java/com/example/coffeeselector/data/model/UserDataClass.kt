package com.example.coffeeselector.data.model


import java.util.Date

/**
 * https://developer.android.com/reference/java/util/Date#Date(int,%20int,%20int)
 */
data class UserDataClass(
    val username: String = "",
    val name: NameDataClass = NameDataClass("", ""),
    val phoneNo: String = "",
    val address: AddressDataClass? = null,
    val dateOfBirth: Date? = null,
    val recentOrders: List<OrderDataClass>? = null,
    val unfinishedOrder: OrderDataClass? = null
)
