package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: Int? = null,
    val userId: Int,
    val username: String? = null,
    val productId: Int,
    val rating: Int,
    val comment: String,
    val createdAt: String? = null
)
