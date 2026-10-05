package com.lyrosmarket.app.data.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.core.SessionManager
import com.lyrosmarket.app.data.remote.DeliveryApiService
import com.lyrosmarket.app.data.remote.dto.toDeliveryOrder
import com.lyrosmarket.app.domain.model.DeliveryOrder
import com.lyrosmarket.app.domain.repository.DeliveryRepository
import io.ktor.client.plugins.*
import java.io.IOException
import com.lyrosmarket.app.core.handleNetworkException

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
            handleNetworkException(e)
        }
    }

    override suspend fun getMyOrders(): Resource<List<DeliveryOrder>> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            val dtos = api.getMyOrders(token)
            Resource.Success(dtos.map { it.toDeliveryOrder() })
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    override suspend fun acceptOrder(orderId: String): Resource<Unit> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            api.acceptOrder(orderId, token)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    override suspend fun updateStatus(orderId: String, status: String): Resource<Unit> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            api.updateStatus(orderId, status, token)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    override suspend fun updateLocation(lat: Double, lng: Double): Resource<Unit> {
        val token = sessionManager.getToken() ?: return Resource.Error("No auth token")
        return try {
            api.updateLocation(lat, lng, token)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

}
