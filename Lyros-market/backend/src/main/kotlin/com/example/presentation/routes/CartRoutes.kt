package com.example.presentation.routes

import com.example.domain.models.CartResponse
import com.example.domain.repositories.CartRepository
import com.example.domain.repositories.ProductRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AddToCartRequest(val productId: Int, val quantity: Int)

@Serializable
data class UpdateQtyRequest(val productId: Int, val quantity: Int)

fun Route.cartRoutes(cartRepository: CartRepository, productRepository: ProductRepository) {
    authenticate {
        route("/cart") {
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    
                    val items = cartRepository.getCart(userId)
                    val total = items.sumOf { it.price * it.quantity }
                    call.respond(CartResponse(items = items, total = total))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }

            post("/add") {
                val rawBody = call.receiveText()
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    
                    val request = Json { ignoreUnknownKeys = true }.decodeFromString<AddToCartRequest>(rawBody)
                    
                    val product = productRepository.getProductById(request.productId)
                    if (product == null) {
                        call.respond(HttpStatusCode.BadRequest, "Product with ID ${request.productId} does not exist.")
                        return@post
                    }

                    cartRepository.addToCart(userId, request.productId, request.quantity)
                    call.respond(HttpStatusCode.OK, "Product added to cart")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Request failed: ${e.message}")
                }
            }

            put("/update-qty") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                    val request = call.receive<UpdateQtyRequest>()
                    cartRepository.updateQuantity(userId, request.productId, request.quantity)
                    call.respond(HttpStatusCode.OK, "Quantity updated")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Update failed")
                }
            }

            delete("/remove/{productId}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val productId = call.parameters["productId"]?.toIntOrNull()
                    if (productId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid product ID")
                        return@delete
                    }
                    cartRepository.removeFromCart(userId, productId)
                    call.respond(HttpStatusCode.OK, "Removed from cart")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }

            delete("/clear") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    cartRepository.clearCart(userId)
                    call.respond(HttpStatusCode.OK, "Cart cleared")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to clear cart")
                }
            }
        }
    }
}
