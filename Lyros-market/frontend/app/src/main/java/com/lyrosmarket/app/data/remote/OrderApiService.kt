package com.lyrosmarket.app.data.remote

import com.lyrosmarket.app.data.remote.dto.OrderDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class OrderApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun getOrders(): List<OrderDto> {
        return client.get("orders").body()
    }

    suspend fun getOrder(orderId: String): OrderDto {
        return client.get("orders/$orderId").body()
    }

    suspend fun checkout(request: com.lyrosmarket.app.data.remote.dto.CheckoutRequest): com.lyrosmarket.app.data.remote.dto.OrderDto {
        return client.post("orders/checkout") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun confirmDelivery(orderId: String) {
        try {
            client.post("orders/$orderId/confirm-delivery")
        } catch (e: Exception) {
            // Fallback to driver status endpoint if client endpoint is missing
            client.put("driver/status/$orderId") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("status" to "CLIENT_CONFIRMED"))
            }
        }
    }
}
