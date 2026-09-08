package com.example.lyrosmarket.data.remote.dto

import kotlinx.serialization.Serializable
import com.example.lyrosmarket.domain.model.Product
import com.example.lyrosmarket.domain.model.Category

@Serializable
data class ProductDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Int,
    val imageUrl: String,
    val stockQuantity: Int,
    val isHighDemand: Boolean = false
)

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String,
    val parentId: Int? = null
)

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        categoryId = categoryId,
        imageUrl = imageUrl,
        stockQuantity = stockQuantity,
        isHighDemand = isHighDemand
    )
}

fun CategoryDto.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        parentId = parentId
    )
}
