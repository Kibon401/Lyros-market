package com.example.presentation.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Int,
    val stockQuantity: Int,
    val imageUrl: String? = null,
    val isHighDemand: Boolean = false // New field
)