package com.example.presentation.routes

import com.example.domain.repositories.AnalyticsRepository
import com.example.domain.repositories.UserRepository
import com.example.domain.repositories.ProductRepository
import com.example.domain.repositories.OrderRepository
import com.example.presentation.requests.CreateProductRequest // Re-using for product update body
import com.example.domain.models.ProductCategory // Re-using for category update body
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RoleUpdateRequest(val role: String)

@Serializable
data class UserUpdateRequest(val username: String, val email: String, val role: String)

fun Route.adminRoutes(
    analyticsRepository: AnalyticsRepository, 
    userRepository: UserRepository,
    productRepository: ProductRepository,
    orderRepository: OrderRepository
) {
    authenticate {
        route("/admin") {
            // Middleware to check if user is ADMIN
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@intercept finish()
                }
            }

            get("/dashboard") {
                val stats = analyticsRepository.getDashboardStats()
                call.respond(stats)
            }

            get("/inventory") {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 1000 // Large default for admin view
                val products = productRepository.getAllProducts(page, size)
                call.respond(products)
            }

            put("/products/{productId}") { 
                val productId = call.parameters["productId"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<CreateProductRequest>() // Re-using DTO
                
                val updated = productRepository.updateProduct(
                    productId,
                    request.name,
                    request.description,
                    request.price,
                    request.categoryId,
                    request.stockQuantity,
                    request.imageUrl,
                    request.isHighDemand // Handle isHighDemand
                )
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Product $productId updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Product not found or no changes made")
                }
            }

            delete("/products/{productId}") { 
                val productId = call.parameters["productId"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val deleted = productRepository.deleteProduct(productId)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Product $productId deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Product not found")
                }
            }

            put("/categories/{categoryId}") { 
                val categoryId = call.parameters["categoryId"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<ProductCategory>() // Re-using DTO
                
                val updated = productRepository.updateCategory(
                    categoryId,
                    request.name,
                    request.description,
                    request.parentId // Handle parentId
                )
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Category $categoryId updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Category not found or no changes made")
                }
            }

            delete("/categories/{categoryId}") { 
                val categoryId = call.parameters["categoryId"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val deleted = productRepository.deleteCategory(categoryId)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Category $categoryId deleted successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                }
            }

            get("/users") { 
                val users = userRepository.getAllUsers()
                // Don't send password hashes to the frontend
                val usersSafe = users.map { it.copy(passwordHash = "") }
                call.respond(usersSafe)
            }

            put("/users/{userId}") { 
                val userId = call.parameters["userId"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UserUpdateRequest>()
                
                val existingUser = userRepository.findUserById(userId)
                if (existingUser == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@put
                }

                userRepository.updateUserByAdmin(userId, request.username, request.email, request.role)
                call.respond(HttpStatusCode.OK, "User $userId updated successfully")
            }

            delete("/users/{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val existingUser = userRepository.findUserById(userId)
                if (existingUser == null) {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                    return@delete
                }
                userRepository.deleteUser(userId)
                call.respond(HttpStatusCode.OK, "User $userId soft-deleted successfully")
            }

            get("/orders") { // New endpoint for admin to view all orders
                val orders = orderRepository.getAllOrders()
                call.respond(orders)
            }

            // Route to promote/demote users (Helper for testing)
            post("/set-role/{userId}") {
                val userId = call.parameters["userId"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<RoleUpdateRequest>()
                val user = userRepository.findUserById(userId) ?: return@post call.respond(HttpStatusCode.NotFound)
                
                userRepository.updateUser(user.copy(role = request.role))
                call.respond(HttpStatusCode.OK, "User $userId role updated to ${request.role}")
            }
        }

        // A one-time "God-mode" route to make yourself admin if you are locked out
        // ONLY FOR DEVELOPMENT
        post("/make-me-admin") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val user = userRepository.findUserById(userId) ?: return@post call.respond(HttpStatusCode.NotFound)
            
            userRepository.updateUser(user.copy(role = "ADMIN"))
            call.respond(HttpStatusCode.OK, "You are now an ADMIN. Please re-login to update your token.")
        }
    }
}
