package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Analytics(
    val totalRevenue: Double,
    val totalOrders: Int,
    val totalUsers: Int,
    val topProducts: List<TopProduct>,
    val userGrowth: List<UserGrowth>
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
