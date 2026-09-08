package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val name: String,
    val description: String,
    val parentId: Int? = null
)

@Serializable
data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Int,
    val stockQuantity: Int,
    val imageUrl: String,
    val isHighDemand: Boolean
)

@Serializable
data class SetRoleRequest(
    val role: String
)

@Serializable
data class UpdateUserRequest(
    val username: String,
    val email: String,
    val role: String
)

@Serializable
data class TopProduct(
    val productId: Int,
    val productName: String,
    val totalSold: Int,
    val revenue: Double
)

@Serializable
data class UserGrowth(
    val date: String,
    val count: Int
)

@Serializable
data class AdminDashboardDto(
    val totalRevenue: Double? = null,
    val totalOrders: Int? = null,
    val totalUsers: Int? = null,
    val activeDrivers: Int? = null,
    val lowStockItems: Int? = null,
    val topProducts: List<TopProduct>? = null,
    val userGrowth: List<UserGrowth>? = null
)

@Serializable
data class AdminUserDto(
    val id: Int,
    val username: String,
    val email: String,
    val role: String? = null
)
