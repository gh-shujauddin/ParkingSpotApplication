package com.qadri.parkingspotapplication.domain.repository

import com.qadri.parkingspotapplication.data.ParkingSpotDao
import com.qadri.parkingspotapplication.data.toParkingSpot
import com.qadri.parkingspotapplication.data.toParkingSpotEntity
import com.qadri.parkingspotapplication.domain.model.ParkingSpot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ParkingSpotRepository {

    suspend fun insertParkingSpot(spot: ParkingSpot)
    suspend fun deleteParkingSpot(spot: ParkingSpot)
    fun getParkingSpot(): Flow<List<ParkingSpot>>
}

class ParkingSpotRepositoryImpl(
    private val dao: ParkingSpotDao
) : ParkingSpotRepository {
    override suspend fun insertParkingSpot(spot: ParkingSpot) =
        dao.insertParkingSpot(spot.toParkingSpotEntity())

    override suspend fun deleteParkingSpot(spot: ParkingSpot) =
        dao.deleteParkingSpot(spot.toParkingSpotEntity())

    override fun getParkingSpot(): Flow<List<ParkingSpot>> =
        dao.getParkingSpots().map { spots ->
            spots.map {
                it.toParkingSpot()
            }
        }
}