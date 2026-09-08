package com.example.lyrosmarket.data.remote.dto

import com.example.lyrosmarket.domain.model.DeliveryOrder
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryOrderDto(
    val id: Int,
    val userId: Int? = null,
    val orderDate: String? = null,
    val subtotal: Double? = null,
    val deliveryFee: Double? = null,
    val totalAmount: Double? = null,
    val status: String? = null,
    val deliveryAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun DeliveryOrderDto.toDeliveryOrder(): DeliveryOrder {
    return DeliveryOrder(
        id = id.toString(),
        date = orderDate ?: "",
        status = status ?: "PENDING",
        customerName = "Customer #$userId", // Backend JSON doesn't provide name currently
        customerPhone = "N/A", // Backend JSON doesn't provide phone currently
        dropOffAddress = deliveryAddress ?: "Unknown Address",
        dropOffLat = latitude,
        dropOffLng = longitude,
        deliveryFee = deliveryFee ?: 0.0,
        distanceKm = null // Compute this client-side if needed, or leave null
    )
}

