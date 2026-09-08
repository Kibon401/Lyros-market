package com.example.domain.repositories

import com.example.domain.models.Order

interface OrderRepository {
    suspend fun createOrder(userId: Int, deliveryAddress: String, latitude: Double?, longitude: Double?): Order?
    suspend fun getOrderById(orderId: Int): Order?
    suspend fun getUserOrders(userId: Int): List<Order>
    suspend fun getAvailableOrders(): List<Order> // For drivers to see
    suspend fun getDriverOrders(driverId: Int): List<Order> // For drivers to see their tasks
    suspend fun updateOrderStatus(orderId: Int, status: String)
    suspend fun assignDriver(orderId: Int, driverId: Int)
    suspend fun cancelExpiredPendingOrders(expirationMinutes: Long) // Corrected method for cron job
    suspend fun getAllOrders(): List<Order> // New method for admin
}
