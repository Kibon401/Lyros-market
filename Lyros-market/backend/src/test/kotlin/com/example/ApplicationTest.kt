package com.example

import com.example.data.database.DatabaseFactory
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.presentation.requests.LoginRequest
import com.example.presentation.requests.RegisterRequest
import com.example.presentation.responses.AuthResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.client.statement.bodyAsText // Added import

class ApplicationTest {

    @Test
    fun testAuthFlow() = testApplication {
        application {
            DatabaseFactory.init(isTest = true)
            configureSerialization()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // 1. Register
        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("testuser", "test@example.com", "password123"))
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)
        val authData = registerResponse.body<AuthResponse>()
        assertNotNull(authData.token)

        // 2. Login
        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("test@example.com", "password123"))
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val loginData = loginResponse.body<AuthResponse>()
        assertEquals("testuser", loginData.username)
    }

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Welcome to Lyros Market API", response.bodyAsText())
    }
}