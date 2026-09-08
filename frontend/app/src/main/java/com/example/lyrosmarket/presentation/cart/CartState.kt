package com.example.lyrosmarket.presentation.cart

import com.example.lyrosmarket.domain.model.CartItem

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val deliveryAddress: String = "123 Market Lane, Nairobi",
    val deliveryFee: Double = 100.0
) {
    val total: Double
        get() = cartItems.sumOf { it.product.price * it.quantity } + deliveryFee
}
