package com.example.lyrosmarket.data.remote

import com.example.lyrosmarket.data.remote.dto.CategoryDto
import com.example.lyrosmarket.data.remote.dto.ProductDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ProductApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun getProducts(page: Int, size: Int, categoryId: Int? = null): List<ProductDto> {
        return client.get("products") {
            parameter("page", page)
            parameter("size", size)
            categoryId?.let { parameter("categoryId", it) }
        }.body<List<ProductDto>>()
    }

    suspend fun getProduct(productId: Int): ProductDto {
        return client.get("products/$productId").body()
    }

    suspend fun searchProducts(query: String, page: Int, size: Int, categoryId: Int? = null): List<ProductDto> {
        return client.get("products/search") {
            parameter("q", query)
            parameter("page", page)
            parameter("size", size)
            categoryId?.let { parameter("categoryId", it) }
        }.body<List<ProductDto>>()
    }

    suspend fun getCategories(): List<CategoryDto> {
        return client.get("categories").body()
    }

    suspend fun getHighDemandProducts(page: Int, size: Int): List<ProductDto> {
        return client.get("products/high-demand") {
            parameter("page", page)
            parameter("size", size)
        }.body<List<ProductDto>>()
    }
}
