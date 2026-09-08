package com.example

import com.example.data.database.DatabaseFactory
import com.example.data.repository.OrderRepositoryImpl
import com.example.plugins.*
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.timer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    // This will load the variables from your .env file for local development
    val dotenv = dotenv {
        directory = "./" // Explicitly set the directory to the project root
        ignoreIfMissing = false // This will make the app crash if the .env file is not found
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") { // Explicitly setting host here
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CallLogging)
    DatabaseFactory.init()
    configureSerialization()
    configureSecurity()
    configureRouting()

    // Schedule cron job to cancel expired pending orders
    val orderRepository = OrderRepositoryImpl()
    timer(
        name = "ExpiredOrderCanceller",
        daemon = true,
        initialDelay = 60 * 1000L, // Start after 1 minute
        period = 5 * 60 * 1000L    // Run every 5 minutes
    ) {
        // Orders pending for more than 30 minutes will be cancelled and restocked
        runBlocking { // Use runBlocking to call suspend function from non-suspend context
        }
    }
}
