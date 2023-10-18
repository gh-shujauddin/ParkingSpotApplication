package com.qadri.parkingspotapplication.presentation

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.qadri.parkingspotapplication.domain.model.ParkingSpot

data class MapState(
    val properties: MapProperties = MapProperties(isMyLocationEnabled = true),
    val parkingSpots: List<ParkingSpot> = listOf(),
    val isFallout: Boolean = false,
    val lastKnownLocation: Location? = null,
    val deviceLatLng: LatLng? = null
)

data class CurrentLocation(
    val latitude: Double? = null,
    val longitude: Double? = null
)
