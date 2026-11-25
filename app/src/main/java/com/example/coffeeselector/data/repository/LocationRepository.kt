package com.example.coffeeselector.data.repository

import com.example.coffeeselector.data.model.LocationDataClass
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class LocationRepository(
    private val database: FirebaseFirestore
){
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