package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.LocationDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class LocationRepository {

    private val database = Firebase.firestore

    suspend fun readLocationDataReturnList(): Triple<Boolean, String, List<Pair<String,LocationDataClass>>?> {
        val onResult: Triple<Boolean, String, List<Pair<String,LocationDataClass>>?> = try {
            val  collection = database.collection("locations").get().await()
            if(!collection.isEmpty) {
                //https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/map-not-null.html
                val locationList = collection.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<LocationDataClass>()?.let {locationData ->
                    Pair(documentSnapshot.id, locationData)
                    }
                }
                Triple(true, "Data fetched successfully", locationList)
            } else Triple(false, "No locations", emptyList())
        } catch (e: Exception) {
            Triple(false, "Error Occurred", null)
        }
        return onResult
    }


}