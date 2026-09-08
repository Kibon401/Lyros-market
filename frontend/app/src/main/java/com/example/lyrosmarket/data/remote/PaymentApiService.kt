package com.example.lyrosmarket.data.remote

import com.example.lyrosmarket.data.remote.dto.StkPushRequest
import com.example.lyrosmarket.data.remote.dto.StkPushResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class PaymentApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun initiateStkPush(request: StkPushRequest): StkPushResponse {
        return client.post("payments/stk-push") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
