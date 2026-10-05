package com.lyrosmarket.app.data.remote.dto

import com.lyrosmarket.app.domain.model.DeliveryOrder
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
    val longitude: Double? = null,
    val customerName: String? = null,
    val customerPhone: String? = null
)

fun DeliveryOrderDto.toDeliveryOrder(): DeliveryOrder {
    return DeliveryOrder(
        id = id.toString(),
        date = orderDate ?: "",
        status = status ?: "PENDING",
        customerName = customerName ?: "Customer #$userId", // Fallback if missing
        customerPhone = customerPhone ?: "N/A", // Fallback if missing
        dropOffAddress = deliveryAddress ?: "Unknown Address",
        dropOffLat = latitude,
        dropOffLng = longitude,
        deliveryFee = deliveryFee ?: 0.0,
        distanceKm = null // Compute this client-side if needed, or leave null
    )
}

