package com.example.lyrosmarket.domain.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val token: String? = null,
    val refreshToken: String? = null
)
