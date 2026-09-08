package com.example.domain.repositories

import com.example.domain.models.DriverLocation

interface DriverLocationRepository {
    suspend fun saveDriverLocation(driverId: Int, latitude: Double, longitude: Double): DriverLocation?
    suspend fun getLastKnownDriverLocation(driverId: Int): DriverLocation?
}
