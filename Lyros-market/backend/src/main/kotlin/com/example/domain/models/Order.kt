package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int? = null,
    val userId: Int,
    val driverId: Int? = null,
    val orderDate: String? = null,
    val subtotal: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val status: String,
    val deliveryAddress: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class OrderItem(
    val id: Int? = null,
    val productId: Int,
    val quantity: Int,
    val price: Double
)
