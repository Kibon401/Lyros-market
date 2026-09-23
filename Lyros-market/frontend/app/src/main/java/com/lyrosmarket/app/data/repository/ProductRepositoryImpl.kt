package com.lyrosmarket.app.data.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.ProductApiService
import com.lyrosmarket.app.data.remote.dto.toCategory
import com.lyrosmarket.app.data.remote.dto.toProduct
import com.lyrosmarket.app.domain.model.Category
import com.lyrosmarket.app.domain.model.Product
import com.lyrosmarket.app.domain.repository.ProductRepository
import io.ktor.client.plugins.*
import java.io.IOException

class ProductRepositoryImpl(
    private val api: ProductApiService
) : ProductRepository {

    override suspend fun getProducts(page: Int, size: Int, categoryId: Int?): Resource<List<Product>> {
        return try {
            val dtos = api.getProducts(page, size, categoryId)
            Resource.Success(dtos.map { it.toProduct() })
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getProduct(productId: Int): Resource<Product> {
        return try {
            val dto = api.getProduct(productId)
            Resource.Success(dto.toProduct())
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun searchProducts(query: String, page: Int, size: Int, categoryId: Int?): Resource<List<Product>> {
        return try {
            val dtos = api.searchProducts(query, page, size, categoryId)
            Resource.Success(dtos.map { it.toProduct() })
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val dtos = api.getCategories()
            Resource.Success(dtos.map { it.toCategory() })
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getHighDemandProducts(page: Int, size: Int): Resource<List<Product>> {
        return try {
            val dtos = api.getHighDemandProducts(page, size)
            Resource.Success(dtos.map { it.toProduct() })
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Resource<T> {
        return when (e) {
            is ResponseException -> Resource.Error(e.response.status.description)
            is IOException -> Resource.Error("Network error")
            else -> Resource.Error(e.message ?: "Unknown error")
        }
    }
}
