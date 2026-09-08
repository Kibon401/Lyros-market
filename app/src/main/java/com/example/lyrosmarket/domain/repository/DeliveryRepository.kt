package com.example.lyrosmarket.domain.repository

import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.domain.model.DeliveryOrder

interface DeliveryRepository {
    suspend fun getAvailableOrders(): Resource<List<DeliveryOrder>>
    suspend fun getMyOrders(): Resource<List<DeliveryOrder>>
    suspend fun acceptOrder(orderId: String): Resource<Unit>
    suspend fun updateStatus(orderId: String, status: String): Resource<Unit>
    suspend fun updateLocation(lat: Double, lng: Double): Resource<Unit>
}
