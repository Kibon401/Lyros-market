package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.model.Category
import com.lyrosmarket.app.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(page: Int, size: Int, categoryId: Int? = null): Resource<List<Product>>
    suspend fun getProduct(productId: Int): Resource<Product>
    suspend fun searchProducts(query: String, page: Int, size: Int, categoryId: Int? = null): Resource<List<Product>>
    suspend fun getCategories(): Resource<List<Category>>
    suspend fun getHighDemandProducts(page: Int, size: Int): Resource<List<Product>>
}
