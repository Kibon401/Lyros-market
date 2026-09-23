package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.domain.model.CartItem
import com.lyrosmarket.app.domain.model.Product
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>
    suspend fun addProductToCart(product: Product, quantity: Int = 1)
    suspend fun removeProductFromCart(productId: Int)
    suspend fun updateQuantity(productId: Int, delta: Int)
    suspend fun clearCart()
    suspend fun fetchCart()
}
