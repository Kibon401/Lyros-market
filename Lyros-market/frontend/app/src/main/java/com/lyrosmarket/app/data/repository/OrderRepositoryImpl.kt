package com.lyrosmarket.app.data.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.OrderApiService
import com.lyrosmarket.app.data.remote.dto.OrderDto
import com.lyrosmarket.app.data.remote.dto.OrderItemDto
import com.lyrosmarket.app.domain.model.Order
import com.lyrosmarket.app.domain.model.OrderItem
import com.lyrosmarket.app.domain.repository.OrderRepository
import io.ktor.client.plugins.*
import java.io.IOException
import com.lyrosmarket.app.core.handleNetworkException

class OrderRepositoryImpl(
    private val api: OrderApiService
) : OrderRepository {

    override suspend fun getOrders(): Resource<List<Order>> {
        return try {
            val dtos = api.getOrders()
            Resource.Success(dtos.map { it.toOrder() })
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    override suspend fun getOrder(orderId: String): Resource<Order> {
        return try {
            val dto = api.getOrder(orderId)
            Resource.Success(dto.toOrder())
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    override suspend fun checkout(
        deliveryAddress: String,
        latitude: Double,
        longitude: Double
    ): Resource<Order> {
        return try {
            val request = com.lyrosmarket.app.data.remote.dto.CheckoutRequest(
                deliveryAddress = deliveryAddress,
                latitude = latitude,
                longitude = longitude
            )
            val dto = api.checkout(request)
            Resource.Success(dto.toOrder())
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    override suspend fun confirmDelivery(orderId: String): Resource<Unit> {
        return try {
            api.confirmDelivery(orderId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

    private fun OrderDto.toOrder(): Order {
        return Order(
            id = id.toString(),
            date = date ?: orderDate ?: "",
            status = status,
            totalAmount = totalAmount,
            items = items?.map { it.toOrderItem() } ?: emptyList(),
            shippingAddress = deliveryAddress ?: shippingAddress ?: "",
            shippingFee = deliveryFee ?: shippingFee ?: 0.0
        )
    }

    private fun OrderItemDto.toOrderItem(): OrderItem {
        return OrderItem(
            productId = productId,
            productName = productName ?: "Product",
            quantity = quantity,
            price = price,
            imageUrl = imageUrl ?: ""
        )
    }

}
