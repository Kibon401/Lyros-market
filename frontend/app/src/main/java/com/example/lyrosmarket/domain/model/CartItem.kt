package com.example.lyrosmarket.domain.model

data class CartItem(
    val product: Product,
    val quantity: Int,
    val weight: String, // e.g. "500g", "1kg"
    val producer: String // e.g. "Farm Direct"
)
