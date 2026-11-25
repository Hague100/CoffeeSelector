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


}