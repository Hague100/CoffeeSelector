package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.LocationDataClass
import com.example.coffeeselector.data.model.UserDataClass
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class LocationRepository {

    private val database = Firebase.firestore


    suspend fun readLocationData(locationDocId: String): Result<UserDataClass> {
        return try {
            val document = database.collection("locations").document(locationDocId).get().await()
            if (document.exists()) {
                val locationData = document.toObject<UserDataClass>()
                if (locationData != null) Result.success(locationData)
                else Result.failure(IllegalStateException("Failed to read data"))
            } else Result.failure(NoSuchElementException("No location data"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
//    suspend fun readLocationDataReturnList(): Triple<Boolean, String, List<Pair<String,LocationDataClass>>?> {
//        val onResult: Triple<Boolean, String, List<Pair<String,LocationDataClass>>?> = try {
//            val  collection = database.collection("locations").get().await()
//            if(!collection.isEmpty) {
//                //https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/map-not-null.html
//                val locationList = collection.documents.mapNotNull { documentSnapshot ->
//                    documentSnapshot.toObject<LocationDataClass>()?.let {locationData ->
//                    Pair(documentSnapshot.id, locationData)
//                    }
//                }
//                Triple(true, "Data fetched successfully", locationList)
//            } else Triple(false, "No locations", emptyList())
//        }catch (e: CancellationException) {
//            throw e
//        }catch (e: Exception) {
//            Triple(false, "Error Occurred", null)
//        }
//        return onResult
//    }

    suspend fun readLocations(): Result<List<LocationDataClass>> {
        return try {
            val collection = database.collection("locations").get().await()
            if (!collection.isEmpty) {
                val locationList = collection.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<LocationDataClass>()
                }
                Result.success(locationList)
            } else {
                Result.failure(NoSuchElementException("No locations"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}