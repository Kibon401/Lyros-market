package com.example.lyrosmarket.domain.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Int,
    val imageUrl: String,
    val stockQuantity: Int,
    val isHighDemand: Boolean
)

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val parentId: Int? = null
)
