package com.example.coffeeselector.ui.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.LocationDataClass
import com.example.coffeeselector.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel: ViewModel() {
    private val locationRepository = LocationRepository()
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> get() = _statusMessage.asStateFlow()
    private val _locationsListState = MutableStateFlow<List<Pair<String,LocationDataClass>>?>(emptyList())
    val locationsListData: StateFlow<List<Pair<String,LocationDataClass>>?> get() = _locationsListState.asStateFlow()
    private val _locationDocID = MutableStateFlow<String?>(null)
    val locationDocId: StateFlow<String?> get() = _locationDocID.asStateFlow()
    init {
        getLocationsListData()
    }

    fun getLocationsListData(){
        viewModelScope.launch {
            // TODO:  
        }
    }
}