package com.example.lyrosmarket.data.remote

import com.example.lyrosmarket.data.remote.dto.DeliveryOrderDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class DeliveryApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun getAvailableOrders(token: String): List<DeliveryOrderDto> {
        return client.get("driver/available-orders") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
    
    suspend fun getMyOrders(token: String): List<DeliveryOrderDto> {
        return client.get("driver/my-orders") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun acceptOrder(orderId: String, token: String) {
        client.post("driver/accept/$orderId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    suspend fun updateStatus(orderId: String, status: String, token: String) {
        client.put("driver/status/$orderId") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }
    }

    suspend fun updateLocation(lat: Double, lng: Double, token: String) {
        client.post("driver/location") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(mapOf("latitude" to lat, "longitude" to lng))
        }
    }
}
