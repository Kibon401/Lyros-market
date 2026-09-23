package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.domain.model.User
import com.lyrosmarket.app.core.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(username: String, email: String, password: String): Resource<User>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    suspend fun getProfile(): Resource<User>
    suspend fun updateProfile(username: String, email: String): Resource<User>
    suspend fun resetPassword(email: String, code: String, newPassword: String): Resource<Unit>
    suspend fun forgotPassword(email: String): Resource<Unit>
}
