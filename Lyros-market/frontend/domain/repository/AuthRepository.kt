package com.lyrosmarket.app.domain.repository

import com.lyrosmarket.app.domain.model.User
import com.lyrosmarket.app.core.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun register(username: String, email: String, password: String): Resource<User>
}
