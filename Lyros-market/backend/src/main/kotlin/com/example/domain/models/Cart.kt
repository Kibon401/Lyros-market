package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String? = null
)

@Serializable
data class CartResponse(
    val items: List<CartItem>,
    val total: Double
)
