package com.example.coffeeselector.ui.locations


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.LocationDataClass
import com.example.coffeeselector.data.repository.LocationRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint
import androidx.core.content.edit


class LocationViewModel(
    private val locationRepository: LocationRepository = LocationRepository(Firebase.firestore)
): ViewModel() {
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    private val _locationsListState = MutableStateFlow<List<LocationDataClass>>(emptyList())
    val locationsListData: StateFlow<List<LocationDataClass>> = _locationsListState.asStateFlow()
    private val _locationDocID = MutableStateFlow<String?>(null)
    fun selectLocation(docId: String) {
        _locationDocID.value = docId
    }
    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchUserLocation(context: Context) {
        val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        if (_userLocation.value == null && prefs.contains("last_lat") && prefs.contains("last_lng")) {
            _userLocation.value = GeoPoint(
                prefs.getFloat("last_lat", 0f).toDouble(),
                prefs.getFloat("last_lng", 0f).toDouble()
            )
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMaxUpdates(1).build()

        fusedLocationClient.requestLocationUpdates(request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { loc -> val geo = GeoPoint(loc.latitude, loc.longitude)
                        _userLocation.value = geo
                        prefs.edit { putFloat("last_lat", loc.latitude.toFloat())
                                .putFloat("last_lng", loc.longitude.toFloat()) }
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    init {
        getLocationsListData()
    }

    fun getLocationsListData() {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = null

            locationRepository.readLocations().fold(
                onSuccess = { locations -> _locationsListState.value = locations },
                onFailure = { exception ->
                    _locationsListState.value = emptyList()
                    _statusMessage.value = exception.localizedMessage ?: "Failed to load locations"
                }
            )
            _isLoading.value = false
        }
    }

    fun calculateDistance(userCoordinates: GeoPoint?, cafeCoordinates: GeoPoint?): Int {
        if (userCoordinates == null || cafeCoordinates == null) return -1
        val result = FloatArray(1)
        Location.distanceBetween(
            userCoordinates.latitude,
            userCoordinates.longitude,
            cafeCoordinates.latitude,
            cafeCoordinates.longitude,
            result
        )
        return result[0].toInt()
    }

}