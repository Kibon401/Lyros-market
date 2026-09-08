package com.example.lyrosmarket.data.repository

import com.example.lyrosmarket.data.remote.CartApiService
import com.example.lyrosmarket.data.remote.dto.AddToCartRequest
import com.example.lyrosmarket.data.remote.dto.CartItemDto
import com.example.lyrosmarket.data.remote.dto.UpdateCartQtyRequest
import com.example.lyrosmarket.domain.model.CartItem
import com.example.lyrosmarket.domain.model.Product
import com.example.lyrosmarket.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val api: CartApiService
) : CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            fetchCart()
        }
    }

    override suspend fun fetchCart() {
        try {
            val response = api.getCart()
            _cartItems.value = response.items.map { it.toCartItem() }
        } catch (e: Exception) {
            // Handle error, maybe clear cart or keep local
        }
    }

    override suspend fun addProductToCart(product: Product, quantity: Int) {
        try {
            api.addToCart(AddToCartRequest(product.id, quantity))
            fetchCart()
        } catch (e: Exception) {
            // Fallback or error handling
        }
    }

    override suspend fun removeProductFromCart(productId: Int) {
        try {
            api.removeItem(productId)
            fetchCart()
        } catch (e: Exception) {
        }
    }

    override suspend fun updateQuantity(productId: Int, delta: Int) {
        val currentItem = _cartItems.value.find { it.product.id == productId } ?: return
        val newQty = (currentItem.quantity + delta).coerceAtLeast(1)
        
        try {
            api.updateQuantity(UpdateCartQtyRequest(productId, newQty))
            fetchCart()
        } catch (e: Exception) {
        }
    }

    override suspend fun clearCart() {
        try {
            api.clearCart()
            fetchCart()
        } catch (e: Exception) {
        }
    }

    private fun CartItemDto.toCartItem(): CartItem {
        return CartItem(
            product = Product(
                id = productId,
                name = productName,
                description = "", // Not provided in cart response
                price = price,
                categoryId = categoryId ?: 0,
                imageUrl = imageUrl,
                stockQuantity = 0,
                isHighDemand = false
            ),
            quantity = quantity,
            weight = "1kg",
            producer = "Farm Direct"
        )
    }
}
