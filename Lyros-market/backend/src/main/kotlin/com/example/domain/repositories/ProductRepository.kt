package com.example.domain.repositories

import com.example.domain.models.Product
import com.example.domain.models.ProductCategory

interface ProductRepository {
    suspend fun getAllProducts(page: Int, size: Int): List<Product> // Modified
    suspend fun getProductById(id: Int): Product?
    suspend fun getProductsByCategory(categoryId: Int, page: Int, size: Int): List<Product> // Modified
    suspend fun getAllCategories(): List<ProductCategory>
    suspend fun createProduct(product: Product): Product?
    suspend fun createCategory(category: ProductCategory): ProductCategory?
    suspend fun searchProducts(query: String, page: Int, size: Int): List<Product> // Modified
    suspend fun updateProduct(productId: Int, name: String, description: String, price: Double, categoryId: Int, stockQuantity: Int, imageUrl: String?, isHighDemand: Boolean): Boolean
    suspend fun deleteProduct(productId: Int): Boolean
    suspend fun updateCategory(categoryId: Int, name: String, description: String?, parentId: Int?): Boolean
    suspend fun deleteCategory(categoryId: Int): Boolean
    suspend fun getHighDemandProducts(page: Int, size: Int): List<Product> // Modified
}
