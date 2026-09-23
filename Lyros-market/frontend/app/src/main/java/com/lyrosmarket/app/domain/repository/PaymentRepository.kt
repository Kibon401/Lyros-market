package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.dto.StkPushResponse

interface PaymentRepository {
    suspend fun initiateStkPush(phoneNumber: String, orderId: Int): Resource<StkPushResponse>
}
