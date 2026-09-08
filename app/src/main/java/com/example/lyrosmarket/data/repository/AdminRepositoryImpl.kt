package com.example.lyrosmarket.data.repository

import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.AdminApiService
import com.example.lyrosmarket.data.remote.dto.*
import com.example.lyrosmarket.domain.repository.AdminRepository
import io.ktor.client.plugins.*
import java.io.File
import java.io.IOException

class AdminRepositoryImpl(
    private val api: AdminApiService
) : AdminRepository {

    override suspend fun uploadProductImage(imageFile: File): Resource<String> {
        return try {
            val url = api.uploadProductImage(imageFile)
            Resource.Success(url)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun createCategory(request: CategoryRequest): Resource<CategoryDto> {
        return try {
            val response = api.createCategory(request)
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun updateCategory(categoryId: Int, request: CategoryRequest): Resource<Unit> {
        return try {
            api.updateCategory(categoryId, request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun deleteCategory(categoryId: Int): Resource<Unit> {
        return try {
            api.deleteCategory(categoryId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun viewCategories(): Resource<List<CategoryDto>> {
        return try {
            val response = api.getCategories()
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun createProduct(request: ProductRequest): Resource<ProductDto> {
        return try {
            val response = api.createProduct(request)
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun updateProduct(productId: Int, request: ProductRequest): Resource<Unit> {
        return try {
            api.updateProduct(productId, request)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun deleteProduct(productId: Int): Resource<Unit> {
        return try {
            api.deleteProduct(productId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun viewAllOrders(): Resource<List<OrderDto>> {
        return try {
            val response = api.viewAllOrders()
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun trackOrder(orderId: Int): Resource<OrderDto> {
        return try {
            val response = api.trackOrder(orderId)
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun markOrderPaid(orderId: Int): Resource<Unit> {
        return try {
            api.markOrderPaid(orderId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getDashboard(): Resource<AdminDashboardDto> {
        return try {
            val response = api.getDashboard()
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun viewAllUsers(): Resource<List<AdminUserDto>> {
        return try {
            val response = api.viewAllUsers()
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun updateUser(userId: Int, request: UpdateUserRequest): Resource<AdminUserDto> {
        return try {
            val response = api.updateUser(userId, request)
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun deleteUser(userId: Int): Resource<Unit> {
        return try {
            api.deleteUser(userId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun setUserRole(userId: Int, role: String): Resource<AdminUserDto> {
        return try {
            val response = api.setUserRole(userId, SetRoleRequest(role))
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun viewInventory(): Resource<List<ProductDto>> {
        return try {
            val response = api.viewInventory()
            Resource.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Resource<T> {
        return when (e) {
            is ResponseException -> Resource.Error("Server error: ${e.response.status.value} - ${e.response.status.description}")
            is IOException -> Resource.Error("Network error: Please check your connection")
            else -> Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
