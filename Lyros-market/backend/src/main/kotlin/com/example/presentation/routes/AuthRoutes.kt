package com.example.presentation.routes

import com.example.core.utils.EmailService
import com.example.core.utils.PasswordHasher
import com.example.domain.models.User
import com.example.domain.repositories.UserRepository
import com.example.presentation.requests.LoginRequest
import com.example.presentation.requests.RegisterRequest
import com.example.presentation.responses.AuthResponse
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Serializable
data class ForgotPasswordRequest(val email: String)

@Serializable
data class ResetPasswordRequest(val email: String, val code: String, val newPassword: String)

fun Route.authRoutes(userRepository: UserRepository) {
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtSecret = "secret"

    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        val existingUser = userRepository.findUserByEmail(request.email)
        
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "User with this email already exists")
            return@post
        }

        val newUser = User(
            username = request.username,
            email = request.email,
            passwordHash = PasswordHasher.hashPassword(request.password)
        )

        val createdUser = userRepository.createUser(newUser)
        if (createdUser != null) {
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtDomain)
                .withClaim("userId", createdUser.id)
                .withClaim("role", createdUser.role)
                .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .sign(Algorithm.HMAC256(jwtSecret))

            call.respond(HttpStatusCode.Created, AuthResponse(token, createdUser.id!!, createdUser.username))
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
        }
    }

    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        val user = userRepository.findUserByEmail(request.email)

        if (user == null || !PasswordHasher.checkPassword(request.password, user.passwordHash)) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
            return@post
        }

        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withClaim("userId", user.id)
            .withClaim("role", user.role)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 hours
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(HttpStatusCode.OK, AuthResponse(token, user.id!!, user.username))
    }

    post("/auth/forgot-password") {
        val request = call.receive<ForgotPasswordRequest>()
        val user = userRepository.findUserByEmail(request.email)
        
        if (user != null) {
            val resetCode = (100000..999999).random().toString()
            val expiry = (Clock.System.now() + 15.minutes).toLocalDateTime(TimeZone.currentSystemDefault())
            
            userRepository.saveResetCode(request.email, resetCode, expiry)
            EmailService.sendResetEmail(request.email, resetCode)
            
            call.respond(HttpStatusCode.OK, "If an account exists with that email, a reset code has been sent.")
        } else {
            // Security best practice: don't reveal if user exists or not
            call.respond(HttpStatusCode.OK, "If an account exists with that email, a reset code has been sent.")
        }
    }

    post("/auth/reset-password") {
        val request = call.receive<ResetPasswordRequest>()
        val isValid = userRepository.verifyResetCode(request.email, request.code)
        
        if (isValid) {
            val newHash = PasswordHasher.hashPassword(request.newPassword)
            userRepository.resetPassword(request.email, newHash)
            call.respond(HttpStatusCode.OK, "Password has been successfully reset.")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid or expired reset code.")
        }
    }
}
