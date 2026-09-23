package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.model.DeliveryOrder

interface DeliveryRepository {
    suspend fun getAvailableOrders(): Resource<List<DeliveryOrder>>
    suspend fun getMyOrders(): Resource<List<DeliveryOrder>>
    suspend fun acceptOrder(orderId: String): Resource<Unit>
    suspend fun updateStatus(orderId: String, status: String): Resource<Unit>
    suspend fun updateLocation(lat: Double, lng: Double): Resource<Unit>
}
