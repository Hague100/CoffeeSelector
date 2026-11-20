package com.example.coffeeselector.data.model


import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * https://developer.android.com/reference/java/util/Date#Date(int,%20int,%20int)
 */
data class UserDataClass(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val name: NameDataClass = NameDataClass("", ""),
    val phoneNo: String = "",
    val address: AddressDataClass? = null,
    val dateOfBirth: Date? = null,
    val recentOrders: List<String>? = null,
    val unfinishedOrder: String? = null,
    val notificationsId:  List<String>? = null
)
