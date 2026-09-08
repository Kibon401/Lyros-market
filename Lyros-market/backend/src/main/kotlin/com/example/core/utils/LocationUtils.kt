package com.example.core.utils

import kotlin.math.*

object LocationUtils {
    // Fixed Shamba (Farm) location in Eldoret
    // Currently set to a point near Eldoret Town Center
    private const val SHAMBA_LAT = 0.514277
    private const val SHAMBA_LON = 35.269779

    // Realistic Eldoret Pricing (KES)
    private const val BASE_FEE = 30.0    // Minimum start fee
    private const val PER_KM_FEE = 25.0  // Charge per kilometer

    /**
     * Calculates distance between the Shamba and the Client.
     * Returns distance in kilometers.
     */
    fun calculateDistance(userLat: Double, userLon: Double): Double {
        val r = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(userLat - SHAMBA_LAT)
        val dLon = Math.toRadians(userLon - SHAMBA_LON)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(SHAMBA_LAT)) * cos(Math.toRadians(userLat)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Calculates the delivery fee based on distance from the Shamba.
     */
    fun calculateDeliveryFee(userLat: Double?, userLon: Double?): Double {
        if (userLat == null || userLon == null) return BASE_FEE
        
        val distance = calculateDistance(userLat, userLon)
        val calculatedFee = BASE_FEE + (distance * PER_KM_FEE)
        
        return round(calculatedFee)
    }
}
