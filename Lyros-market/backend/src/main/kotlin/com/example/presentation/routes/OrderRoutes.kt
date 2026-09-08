package com.example.presentation.routes

import com.example.domain.repositories.OrderRepository
import com.example.domain.repositories.DriverLocationRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CheckoutRequest(
    val deliveryAddress: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun Route.orderRoutes(orderRepository: OrderRepository, driverLocationRepository: DriverLocationRepository) { // Added DriverLocationRepository
    authenticate {
        post("/orders/test-mark-paid/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()
            if (role != "ADMIN") {
                call.respond(HttpStatusCode.Forbidden, "Admin access required")
                return@post
            }

            val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
            orderRepository.updateOrderStatus(id, "PAID")
            call.respond(HttpStatusCode.OK, "Order $id marked as PAID for testing")
        }

        route("/orders") {
            post("/checkout") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    
                    val request = call.receive<CheckoutRequest>()
                    val order = orderRepository.createOrder(
                        userId, 
                        request.deliveryAddress,
                        request.latitude,
                        request.longitude
                    )
                    
                    if (order != null) {
                        call.respond(HttpStatusCode.Created, order)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Failed to create order. Is your cart empty?")
                    }
                } catch (e: Exception) {
                    println("DEBUG: Checkout Error: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest, "Checkout failed: Check your JSON body")
                }
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val orders = orderRepository.getUserOrders(userId)
                call.respond(orders)
            }

            get("/{id}") {
                val idStr = call.parameters["id"]
                if (idStr == "checkout") {
                    call.respond(HttpStatusCode.MethodNotAllowed, "Use POST for /orders/checkout")
                    return@get
                }
                
                val id = idStr?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid Order ID")
                val order = orderRepository.getOrderById(id)
                if (order != null) {
                    call.respond(order)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            get("/{id}/track") { // New endpoint
                val orderId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val role = principal?.payload?.getClaim("role")?.asString()

                val order = orderRepository.getOrderById(orderId) ?: return@get call.respond(HttpStatusCode.NotFound, "Order not found")

                // Security check: Only the order owner or an Admin can track
                if (order.userId != userId && role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "You can only track your own orders")
                    return@get
                }

                val driverId = order.driverId
                if (driverId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Order not yet assigned to a driver")
                    return@get
                }

                val driverLocation = driverLocationRepository.getLastKnownDriverLocation(driverId)
                if (driverLocation != null) {
                    call.respond(HttpStatusCode.OK, driverLocation)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Driver location not available")
                }
            }
        }
    }
}
