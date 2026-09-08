package com.example.lyrosmarket.data.remote

import com.example.lyrosmarket.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class CartApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun addToCart(request: AddToCartRequest): HttpResponse {
        return client.post("cart/add") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun updateQuantity(request: UpdateCartQtyRequest): HttpResponse {
        return client.put("cart/update-qty") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getCart(): CartResponse {
        return client.get("cart").body()
    }

    suspend fun removeItem(productId: Int): HttpResponse {
        return client.delete("cart/remove/$productId")
    }

    suspend fun clearCart(): HttpResponse {
        return client.delete("cart/clear")
    }
}
