package com.lyrosmarket.app.data.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.PaymentApiService
import com.lyrosmarket.app.data.remote.dto.StkPushRequest
import com.lyrosmarket.app.data.remote.dto.StkPushResponse
import com.lyrosmarket.app.domain.repository.PaymentRepository
import io.ktor.client.plugins.*
import java.io.IOException
import com.lyrosmarket.app.core.handleNetworkException

class PaymentRepositoryImpl(
    private val api: PaymentApiService
) : PaymentRepository {

    override suspend fun initiateStkPush(
        phoneNumber: String,
        orderId: Int
    ): Resource<StkPushResponse> {
        return try {
            val response = api.initiateStkPush(
                StkPushRequest(orderId, phoneNumber)
            )
            // Daraja ResponseCode "0" means success
            if (response.ResponseCode == "0" || response.success) {
                Resource.Success(response)
            } else {
                Resource.Error(response.ResponseDescription ?: response.CustomerMessage ?: response.message ?: "Payment initiation failed")
            }
        } catch (e: Exception) {
            handleNetworkException(e)
        }
    }

}
