package com.lyrosmarket.app.presentation.cart

import com.lyrosmarket.app.domain.model.CartItem
import com.lyrosmarket.app.data.remote.dto.AddressDto

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val deliveryAddress: String = "123 Market Lane, Nairobi",
    val deliveryFee: Double = 100.0,
    val selectedAddress: AddressDto? = null
) {
    val total: Double
        get() = cartItems.sumOf { it.product.price * it.quantity } + (selectedAddress?.shippingFee ?: deliveryFee)
}
