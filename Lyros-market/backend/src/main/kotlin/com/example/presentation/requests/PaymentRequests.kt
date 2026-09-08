package com.example.presentation.requests

import kotlinx.serialization.Serializable

@Serializable
data class StkPushRequest(
    val orderId: Int,
    val phoneNumber: String // Format: 2547XXXXXXXX
)

@Serializable
data class DarajaAuthResponse(
    val access_token: String,
    val expires_in: String
)

@Serializable
data class DarajaStkResponse(
    val MerchantRequestID: String,
    val CheckoutRequestID: String,
    val ResponseCode: String,
    val ResponseDescription: String,
    val CustomerMessage: String
)
