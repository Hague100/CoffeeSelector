package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.LocationDataClass
import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firestore.v1.Document

class LocationRepository {

    private val database = Firebase.firestore
    private val  locationCollection = database.collection("locations")

//    fun readLocationDataReturnList(): List<LocationDataClass> {
//
//    }
}