package com.example.lyrosmarket.domain.repository

import com.example.lyrosmarket.domain.model.User
import com.example.lyrosmarket.core.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(username: String, email: String, password: String): Resource<User>
}
