package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: Int? = null,
    val label: String, // e.g., "Home", "Work"
    val fullAddress: String,
    val instructions: String? = null,
    val isDefault: Boolean = false,
    val shippingFee: Double = 0.0,
    val latitude: Double? = null,
    val longitude: Double? = null
)
