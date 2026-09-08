package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.lyrosmarket.domain.model.User

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    @SerialName("userId") val id: Int? = null,
    val username: String? = null,
    val email: String? = null,
    val role: String? = null,
    val token: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null
)

fun AuthResponse.toUser(): User {
    return User(
        id = id ?: 0,
        username = username ?: "",
        email = email ?: "",
        role = role ?: "USER",
        token = token ?: "",
        refreshToken = refreshToken
    )
}
