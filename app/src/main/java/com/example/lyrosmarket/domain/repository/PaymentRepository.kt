package com.example.lyrosmarket.domain.repository

import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.dto.StkPushResponse

interface PaymentRepository {
    suspend fun initiateStkPush(phoneNumber: String, orderId: Int): Resource<StkPushResponse>
}
