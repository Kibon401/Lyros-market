package com.example.presentation.routes

import com.example.domain.repositories.OrderRepository
import com.example.domain.repositories.UserRepository
import com.example.domain.repositories.DriverLocationRepository
import com.example.presentation.requests.DriverLocationUpdateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class StatusUpdateRequest(val status: String)

fun Route.driverRoutes(
    orderRepository: OrderRepository, 
    userRepository: UserRepository,
    driverLocationRepository: DriverLocationRepository // Added
) {
    authenticate {
        route("/driver") {
            // Intercept all routes within this block to check for DRIVER role
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != "DRIVER") {
                    call.respond(HttpStatusCode.Forbidden, "Driver access required")
                    return@intercept finish()
                }
            }

            // 1. Get all PAID orders waiting for a driver
            get("/available-orders") {
                val orders = orderRepository.getAvailableOrders()
                call.respond(orders)
            }

            // 2. Accept an order
            post("/accept/{orderId}") {
                val principal = call.principal<JWTPrincipal>()
                val driverId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                
                val orderId = call.parameters["orderId"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                
                val order = orderRepository.getOrderById(orderId)
                if (order == null) {
                    call.respond(HttpStatusCode.NotFound, "Order not found")
                } else if (order.status != "PAID") {
                    call.respond(HttpStatusCode.BadRequest, "Order is not ready for pickup (Status: ${order.status})")
                } else {
                    orderRepository.assignDriver(orderId, driverId)
                    call.respond(HttpStatusCode.OK, "Order accepted. Navigation to ${order.deliveryAddress} started.")
                }
            }

            // 3. Update delivery status (PICKED_UP, DELIVERED)
            put("/status/{orderId}") {
                val principal = call.principal<JWTPrincipal>()
                val driverId = principal?.payload?.getClaim("userId")?.asInt() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                
                val orderId = call.parameters["orderId"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<StatusUpdateRequest>()
                
                val order = orderRepository.getOrderById(orderId)
                if (order == null || order.driverId != driverId) {
                    call.respond(HttpStatusCode.Forbidden, "You are not assigned to this order")
                } else {
                    orderRepository.updateOrderStatus(orderId, request.status)
                    call.respond(HttpStatusCode.OK, "Status updated to ${request.status}")
                }
            }

            // 4. My assigned tasks
            get("/my-orders") {
                val principal = call.principal<JWTPrincipal>()
                val driverId = principal?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val orders = orderRepository.getDriverOrders(driverId)
                call.respond(orders)
            }

            // 5. Driver sends location update
            post("/location") {
                val principal = call.principal<JWTPrincipal>()
                val driverId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<DriverLocationUpdateRequest>()

                driverLocationRepository.saveDriverLocation(driverId, request.latitude, request.longitude)
                call.respond(HttpStatusCode.OK, "Location updated")
            }
        }
    }
}
