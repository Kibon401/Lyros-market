package com.example.plugins

import com.example.data.repository.*
import com.example.domain.repositories.DriverLocationRepository
import com.example.presentation.routes.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    val userRepository = UserRepositoryImpl()
    val productRepository = ProductRepositoryImpl()
    val cartRepository = CartRepositoryImpl()
    val orderRepository = OrderRepositoryImpl()
    val reviewRepository = ReviewRepositoryImpl()
    val analyticsRepository = AnalyticsRepositoryImpl()
    val driverLocationRepository = DriverLocationRepositoryImpl()
    
    routing {
        get("/") {
            call.respondText("Welcome to Lyros Market API")
        }
        
        // Static files route to serve uploaded images
        staticFiles("/uploads", File("uploads"))
        
        authRoutes(userRepository)
        userRoutes(userRepository)
        productRoutes(productRepository)
        cartRoutes(cartRepository, productRepository)
        orderRoutes(orderRepository, driverLocationRepository) // Pass new repository
        paymentRoutes(orderRepository)
        driverRoutes(orderRepository, userRepository, driverLocationRepository)
        reviewRoutes(reviewRepository)
        adminRoutes(analyticsRepository, userRepository, productRepository, orderRepository)
        uploadRoutes()
    }
}
