package com.example.domain.repositories

import com.example.domain.models.Analytics

interface AnalyticsRepository {
    suspend fun getDashboardStats(): Analytics
}
