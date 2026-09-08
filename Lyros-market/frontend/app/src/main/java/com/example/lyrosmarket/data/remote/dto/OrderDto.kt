package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: Int,
    val userId: Int? = null,
    val orderDate: String? = null,
    val date: String? = null, // Backend might use 'date' or 'orderDate'
    val status: String,
    val totalAmount: Double,
    val subtotal: Double? = null,
    val deliveryFee: Double? = null,
    val items: List<OrderItemDto>? = emptyList(),
    val shippingAddress: String? = null,
    val deliveryAddress: String? = null, // Backend uses 'deliveryAddress' in checkout response
    val shippingFee: Double? = null,
    val paymentStatus: String? = null
)

@Serializable
data class OrderItemDto(
    val id: Int? = null,
    val productId: Int,
    val productName: String? = null,
    val quantity: Int,
    val price: Double,
    val imageUrl: String? = null
)

@Serializable
data class CheckoutRequest(
    val deliveryAddress: String,
    val latitude: Double,
    val longitude: Double
)

