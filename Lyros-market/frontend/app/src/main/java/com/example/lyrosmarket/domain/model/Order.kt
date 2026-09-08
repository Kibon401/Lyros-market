package com.example.lyrosmarket.domain.model

data class Order(
    val id: String,
    val date: String,
    val status: String,
    val totalAmount: Double,
    val items: List<OrderItem>,
    val shippingAddress: String,
    val shippingFee: Double
)

data class OrderItem(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String
)
