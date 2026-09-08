package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductCategory(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val parentId: Int? = null // New field for hierarchical categories
)

@Serializable
data class Product(
    val id: Int? = null,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Int,
    val stockQuantity: Int,
    val imageUrl: String? = null,
    val isHighDemand: Boolean = false // New field for high demand products
)