package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    val productId: Int,
    val quantity: Int
)

@Serializable
data class UpdateCartQtyRequest(
    val productId: Int,
    val quantity: Int
)

@Serializable
data class CartResponse(
    val items: List<CartItemDto>,
    val total: Double? = null,
    val totalAmount: Double? = null
)

@Serializable
data class CartItemDto(
    val productId: Int,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val categoryId: Int? = null
)
