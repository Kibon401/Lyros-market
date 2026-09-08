package com.example.lyrosmarket.domain.repository

import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.domain.model.Order

interface OrderRepository {
    suspend fun getOrders(): Resource<List<Order>>
    suspend fun getOrder(orderId: String): Resource<Order>
    suspend fun checkout(
        deliveryAddress: String,
        latitude: Double,
        longitude: Double
    ): Resource<Order>
}
