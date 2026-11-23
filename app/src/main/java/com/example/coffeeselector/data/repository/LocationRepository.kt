package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.LocationDataClass
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class LocationRepository(
    private val database: FirebaseFirestore
){
    suspend fun readLocationData(docId: String): Result<LocationDataClass> {
        return try {
            val documentSnapshot = database.collection("locations").document(docId).get().await()
            val location = documentSnapshot.toObject<LocationDataClass>()
            if (documentSnapshot.exists() && location != null) Result.success(location)
            else Result.failure(NoSuchElementException("No location data"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun readLocations(): Result<List<LocationDataClass>> {
        return try {
            val collection = database.collection("locations").get().await()
            if (!collection.isEmpty) {
                val locationList = collection.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject<LocationDataClass>()
                }
                Result.success(locationList)
            } else Result.failure(NoSuchElementException("No locations"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

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