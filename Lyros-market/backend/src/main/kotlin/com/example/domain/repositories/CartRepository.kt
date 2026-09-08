package com.example.domain.repositories

import com.example.domain.models.CartItem

interface CartRepository {
    suspend fun getCart(userId: Int): List<CartItem>
    suspend fun addToCart(userId: Int, productId: Int, quantity: Int)
    suspend fun removeFromCart(userId: Int, productId: Int)
    suspend fun updateQuantity(userId: Int, productId: Int, quantity: Int)
    suspend fun clearCart(userId: Int)
}
