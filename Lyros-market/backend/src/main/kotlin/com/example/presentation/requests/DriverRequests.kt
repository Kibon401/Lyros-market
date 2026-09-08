package com.example.presentation.requests

import kotlinx.serialization.Serializable

@Serializable
data class DriverLocationUpdateRequest(
    val latitude: Double,
    val longitude: Double
)
