package com.example.lyrosmarket.data.remote

import com.example.lyrosmarket.data.remote.dto.OrderDto
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

    suspend fun checkout(request: com.example.lyrosmarket.data.remote.dto.CheckoutRequest): com.example.lyrosmarket.data.remote.dto.OrderDto {
        return client.post("orders/checkout") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
