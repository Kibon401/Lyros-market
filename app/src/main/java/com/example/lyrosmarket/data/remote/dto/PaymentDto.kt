package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StkPushRequest(
    val orderId: Int,
    val phoneNumber: String
)

@Serializable
data class StkPushResponse(
    val MerchantRequestID: String? = null,
    val CheckoutRequestID: String? = null,
    val ResponseCode: String? = null,
    val ResponseDescription: String? = null,
    val CustomerMessage: String? = null,
    val success: Boolean = true, // Keeping for backward compatibility if needed
    val message: String? = null
)
