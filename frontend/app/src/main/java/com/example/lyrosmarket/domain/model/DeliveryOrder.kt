package com.example.lyrosmarket.domain.model

data class DeliveryOrder(
    val id: String,
    val date: String,
    val status: String, // PENDING, ACCEPTED, PICKED_UP, DELIVERED
    val customerName: String,
    val customerPhone: String,
    val dropOffAddress: String,
    val dropOffLat: Double?,
    val dropOffLng: Double?,
    val deliveryFee: Double,
    val distanceKm: Double?
)
