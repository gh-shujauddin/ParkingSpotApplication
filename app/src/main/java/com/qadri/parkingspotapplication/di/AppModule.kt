package com.qadri.parkingspotapplication.di

import android.app.Application
import androidx.room.Room
import com.qadri.parkingspotapplication.data.ParkingSpotDatabase
import com.qadri.parkingspotapplication.domain.repository.ParkingSpotRepository
import com.qadri.parkingspotapplication.domain.repository.ParkingSpotRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideParkingSpotDatabase(app: Application): ParkingSpotDatabase =
        Room.databaseBuilder(app, ParkingSpotDatabase::class.java, "parking_spots.db")
            .build()

    @Singleton
    @Provides
    fun providesParkingSpotRepository(db: ParkingSpotDatabase): ParkingSpotRepository {
        return ParkingSpotRepositoryImpl(db.dao)
    }
}

