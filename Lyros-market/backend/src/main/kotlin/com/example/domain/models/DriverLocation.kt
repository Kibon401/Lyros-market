package com.example.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class DriverLocation(
    val id: Int? = null,
    val driverId: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime
)
