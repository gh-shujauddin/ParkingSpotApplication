package com.qadri.parkingspotapplication.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.MapStyleOptions
import com.qadri.parkingspotapplication.domain.model.ParkingSpot
import com.qadri.parkingspotapplication.domain.repository.ParkingSpotRepository
import com.qadri.parkingspotapplication.presentation.MapEvent
import com.qadri.parkingspotapplication.presentation.MapState
import com.qadri.parkingspotapplication.presentation.MapStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

    @HiltViewModel
    class MapsViewModel @Inject constructor(
        private val repository: ParkingSpotRepository
    ) : ViewModel() {

        var state by mutableStateOf(MapState())

        init {
            viewModelScope.launch {
                repository.getParkingSpot().collectLatest {
                    state = state.copy(
                        parkingSpots = it
                    )
                }
            }
        }


        fun onEvent(event: MapEvent) {
            when (event) {
                is MapEvent.ToggleFalloutMap -> {
                    state = state.copy(
                        properties = state.properties.copy(
                            mapStyleOptions =
                            if (state.isFallout)
                                null
                            else
                                MapStyleOptions(MapStyle.json)
                        ),
                        isFallout = !state.isFallout
                    )
                }

                is MapEvent.OnMapLongClick -> {
                    viewModelScope.launch {
                        repository.insertParkingSpot(
                            ParkingSpot(
                                event.latLng.latitude,
                                event.latLng.longitude
                            )
                        )
                    }
                }

                is MapEvent.OnInfoWindowLongClick -> {
                    viewModelScope.launch {
                        repository.deleteParkingSpot(event.spot)
                    }
                }
            }
        }
    }