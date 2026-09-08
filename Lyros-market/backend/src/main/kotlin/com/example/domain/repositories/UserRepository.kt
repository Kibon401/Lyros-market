package com.example.domain.repositories

import com.example.domain.models.User
import kotlinx.datetime.LocalDateTime

interface UserRepository {
    suspend fun findUserByEmail(email: String): User?
    suspend fun createUser(user: User): User?
    suspend fun findUserById(id: Int): User?
    suspend fun updateUser(user: User) // For user's own profile update
    suspend fun saveResetCode(email: String, code: String, expiry: LocalDateTime)
    suspend fun verifyResetCode(email: String, code: String): Boolean
    suspend fun resetPassword(email: String, newPasswordHash: String)
    suspend fun getAllUsers(): List<User>
    suspend fun updateUserByAdmin(userId: Int, username: String, email: String, role: String)
    suspend fun deleteUser(userId: Int) // New method for soft delete
}
