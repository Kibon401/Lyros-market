package com.lyrosmarket.app.data.repository

import com.lyrosmarket.app.core.SessionManager
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.AuthApiService
import com.lyrosmarket.app.data.remote.dto.LoginRequest
import com.lyrosmarket.app.data.remote.dto.RegisterRequest
import com.lyrosmarket.app.data.remote.dto.toUser
import com.lyrosmarket.app.domain.model.User
import com.lyrosmarket.app.domain.repository.AuthRepository
import io.ktor.client.plugins.*
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val token = response.token ?: throw Exception("No token received from login")
            
            sessionManager.saveToken(token)
            response.refreshToken?.let { sessionManager.saveRefreshToken(it) }
            
            // Now that token is saved, fetch full profile (this will use the token)
            val profile = api.getProfile()
            
            sessionManager.saveUserEmail(profile.email ?: email)
            sessionManager.saveUserName(profile.username ?: "User")
            sessionManager.saveLoginTimestamp()
            sessionManager.saveUserRole(profile.role ?: "USER")
            
            val user = profile.toUser().copy(token = token)
            Resource.Success(user)
        } catch (e: ResponseException) {
            Resource.Error(e.response.status.description)
        } catch (e: IOException) {
            Resource.Error("Could not reach server: ${e.message}")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun register(username: String, email: String, password: String): Resource<User> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))
            val token = response.token ?: throw Exception("No token received from register")
            
            sessionManager.saveToken(token)
            response.refreshToken?.let { sessionManager.saveRefreshToken(it) }
            
            val profile = try {
                api.getProfile()
            } catch (e: Exception) {
                response // Fallback to register response if profile fails right after register
            }
            
            sessionManager.saveUserEmail(email)
            sessionManager.saveUserName(username)
            sessionManager.saveLoginTimestamp()
            sessionManager.saveUserRole(profile.role ?: "USER")
            
            val user = profile.toUser().copy(token = token, username = username, email = email)
            Resource.Success(user)
        } catch (e: ResponseException) {
            Resource.Error(e.response.status.description)
        } catch (e: IOException) {
            Resource.Error("Could not reach server: ${e.message}")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override fun isLoggedIn(): Boolean {
        return sessionManager.getToken() != null
    }

    override suspend fun getProfile(): Resource<User> {
        return try {
            val response = api.getProfile()
            Resource.Success(response.toUser())
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun updateProfile(username: String, email: String): Resource<User> {
        return try {
            val response = api.updateProfile(username, email)
            Resource.Success(response.toUser())
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Resource<Unit> {
        return try {
            api.resetPassword(com.lyrosmarket.app.data.remote.dto.ResetPasswordRequest(email, code, newPassword))
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun forgotPassword(email: String): Resource<Unit> {
        return try {
            api.forgotPassword(mapOf("email" to email))
            Resource.Success(Unit)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Resource<T> {
        return when (e) {
            is ResponseException -> Resource.Error("Server error: ${e.response.status.value}")
            is IOException -> Resource.Error("Network error: Please check your connection")
            else -> Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
