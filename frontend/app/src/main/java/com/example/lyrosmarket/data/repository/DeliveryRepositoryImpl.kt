package com.example.lyrosmarket.data.repository

import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.core.SessionManager
import com.example.lyrosmarket.data.remote.DeliveryApiService
import com.example.lyrosmarket.data.remote.dto.toDeliveryOrder
import com.example.lyrosmarket.domain.model.DeliveryOrder
import com.example.lyrosmarket.domain.repository.DeliveryRepository
import io.ktor.client.plugins.*
import java.io.IOException

class DeliveryRepositoryImpl(
    private val api: DeliveryApiService,
    private val sessionManager: SessionManager
) : DeliveryRepository {

    override suspend fun getAvailableOrders(): Resource<List<DeliveryOrder>> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            val dtos = api.getAvailableOrders(token)
            Resource.Success(dtos.map { it.toDeliveryOrder() })
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getMyOrders(): Resource<List<DeliveryOrder>> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            val dtos = api.getMyOrders(token)
            Resource.Success(dtos.map { it.toDeliveryOrder() })
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun acceptOrder(orderId: String): Resource<Unit> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            api.acceptOrder(orderId, token)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun updateStatus(orderId: String, status: String): Resource<Unit> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            api.updateStatus(orderId, status, token)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun updateLocation(lat: Double, lng: Double): Resource<Unit> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            api.updateLocation(lat, lng, token)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Resource<T> {
        return when (e) {
            is ResponseException -> Resource.Error(e.response.status.description)
            is IOException -> Resource.Error("Network error")
            else -> Resource.Error(e.message ?: "Unknown error")
        }
    }
}
