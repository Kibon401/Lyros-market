package com.example.presentation.routes

import com.example.domain.repositories.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AddressUpdateRequest(val address: String)

@Serializable
data class ProfileUpdateRequest(val username: String, val email: String)

fun Route.userRoutes(userRepository: UserRepository) {
    authenticate {
        route("/users") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val user = userRepository.findUserById(userId)
                
                if (user != null) {
                    // Don't send the password hash
                    val profile = user.copy(passwordHash = "")
                    call.respond(profile)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            put("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asInt()
                val request = call.receive<ProfileUpdateRequest>()
                
                val user = userRepository.findUserById(userId)
                if (user != null) {
                    userRepository.updateUser(user.copy(
                        username = request.username,
                        email = request.email
                    ))
                    call.respond(HttpStatusCode.OK, "Profile updated")
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("/change-address") {
                // In a real app, you'd store this in a UserAddress table
                // For now, we'll just acknowledge the request as a placeholder
                val request = call.receive<AddressUpdateRequest>()
                call.respond(HttpStatusCode.OK, "Default address updated to: ${request.address}")
            }

            delete("/delete-account") {
                // Placeholder for GDPR compliance
                call.respond(HttpStatusCode.OK, "Account deletion request received and is being processed.")
            }
        }
    }
}
