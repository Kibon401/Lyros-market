package com.example.lyrosmarket.data.remote

import com.example.lyrosmarket.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File

class AdminApiService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    // Images & Products
    suspend fun uploadProductImage(imageFile: File): String {
        val response: Map<String, String> = client.post("upload/product-image") {
            setBody(MultiPartFormDataContent(
                formData {
                    append("image", imageFile.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg") // Or appropriate type
                        append(HttpHeaders.ContentDisposition, "filename=\"${imageFile.name}\"")
                    })
                }
            ))
        }.body()
        return response["imageUrl"] ?: throw Exception("Failed to get image URL")
    }

    suspend fun createCategory(request: CategoryRequest): CategoryDto {
        return client.post("categories") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateCategory(categoryId: Int, request: CategoryRequest): String {
        return client.put("admin/categories/$categoryId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteCategory(categoryId: Int) {
        client.delete("admin/categories/$categoryId")
    }

    suspend fun getCategories(): List<CategoryDto> {
        return client.get("categories").body()
    }

    suspend fun createProduct(request: ProductRequest): ProductDto {
        return client.post("products") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateProduct(productId: Int, request: ProductRequest): String {
        return client.put("admin/products/$productId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteProduct(productId: Int) {
        client.delete("admin/products/$productId")
    }

    // Orders
    suspend fun viewAllOrders(): List<OrderDto> { // Note: using OrderDto from OrderApiService
        return client.get("admin/orders").body()
    }

    suspend fun trackOrder(orderId: Int): OrderDto {
        return client.get("orders/$orderId/track").body()
    }

    suspend fun markOrderPaid(orderId: Int) {
        client.post("orders/test-mark-paid/$orderId")
    }

    // Dashboard & Users
    suspend fun getDashboard(): AdminDashboardDto {
        return client.get("admin/dashboard").body()
    }

    suspend fun viewAllUsers(): List<AdminUserDto> {
        return client.get("admin/users").body()
    }

    suspend fun updateUser(userId: Int, request: UpdateUserRequest): AdminUserDto {
        return client.put("admin/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteUser(userId: Int) {
        client.delete("admin/users/$userId")
    }

    suspend fun setUserRole(userId: Int, request: SetRoleRequest): AdminUserDto {
        return client.post("admin/set-role/$userId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Inventory
    suspend fun viewInventory(): List<ProductDto> {
        return client.get("admin/inventory").body()
    }
}
