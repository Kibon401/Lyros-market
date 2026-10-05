package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.model.Order

interface OrderRepository {
    suspend fun getOrders(): Resource<List<Order>>
    suspend fun getOrder(orderId: String): Resource<Order>
    suspend fun checkout(
        deliveryAddress: String,
        latitude: Double,
        longitude: Double
    ): Resource<Order>
    suspend fun confirmDelivery(orderId: String): Resource<Unit>
}
