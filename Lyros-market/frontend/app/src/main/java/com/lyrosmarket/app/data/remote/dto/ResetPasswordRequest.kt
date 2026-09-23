package com.lyrosmarket.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)
