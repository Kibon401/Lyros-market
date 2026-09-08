package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String = "USER",
    val isActive: Boolean = true // New field for soft delete
)