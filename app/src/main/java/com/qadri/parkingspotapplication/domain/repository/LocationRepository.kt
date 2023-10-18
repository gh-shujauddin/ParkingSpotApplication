package com.qadri.parkingspotapplication.domain.repository

import android.content.Context
import android.location.LocationManager
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class LocationRepository(val context: Context) {
    val gpsStatus = flow {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        while (currentCoroutineContext().isActive) {
            emit(
                manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            )
            delay(3000)
        }
    }
}