package com.example.lyrosmarket.domain.repository

import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.dto.*
import java.io.File

interface AdminRepository {
    suspend fun uploadProductImage(imageFile: File): Resource<String>
    suspend fun createCategory(request: CategoryRequest): Resource<CategoryDto>
    suspend fun updateCategory(categoryId: Int, request: CategoryRequest): Resource<Unit>
    suspend fun deleteCategory(categoryId: Int): Resource<Unit>
    suspend fun viewCategories(): Resource<List<CategoryDto>>
    
    suspend fun createProduct(request: ProductRequest): Resource<ProductDto>
    suspend fun updateProduct(productId: Int, request: ProductRequest): Resource<Unit>
    suspend fun deleteProduct(productId: Int): Resource<Unit>
    
    suspend fun viewAllOrders(): Resource<List<OrderDto>>
    suspend fun trackOrder(orderId: Int): Resource<OrderDto>
    suspend fun markOrderPaid(orderId: Int): Resource<Unit>
    
    suspend fun getDashboard(): Resource<AdminDashboardDto>
    suspend fun viewAllUsers(): Resource<List<AdminUserDto>>
    suspend fun updateUser(userId: Int, request: UpdateUserRequest): Resource<AdminUserDto>
    suspend fun deleteUser(userId: Int): Resource<Unit>
    suspend fun setUserRole(userId: Int, role: String): Resource<AdminUserDto>
    
    suspend fun viewInventory(): Resource<List<ProductDto>>
}
