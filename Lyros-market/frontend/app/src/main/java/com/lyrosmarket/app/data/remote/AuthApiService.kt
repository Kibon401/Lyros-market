package com.lyrosmarket.app.data.remote

import com.lyrosmarket.app.data.remote.dto.AuthResponse
import com.lyrosmarket.app.data.remote.dto.LoginRequest
import com.lyrosmarket.app.data.remote.dto.RegisterRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(
    private val client: HttpClient,
    private val baseUrl: String // Keeping this for reference, but will use relative paths
) {
    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getProfile(): AuthResponse {
        return client.get("users/profile").body()
    }

    suspend fun updateProfile(username: String, email: String): AuthResponse {
        return client.put("users/profile") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "email" to email))
        }.body()
    }

    suspend fun resetPassword(request: com.lyrosmarket.app.data.remote.dto.ResetPasswordRequest): AuthResponse {
        return client.post("auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun forgotPassword(request: Map<String, String>): AuthResponse {
        return client.post("auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
