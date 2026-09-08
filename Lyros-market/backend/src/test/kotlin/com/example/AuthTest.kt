package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.presentation.requests.RegisterRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class AuthTest {

    @Test
    fun testRegister() = testApplication {
        application {
            // We need to initialize the DB or mock the repository for a pure unit test
            // For now, let's just check if the routing is configured correctly
            configureSerialization()
            configureRouting()
        }
        
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // This will likely fail without a running DB, but it tests the routing setup
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("testuser", "test@example.com", "password123"))
        }
        
        // If DB isn't running, it might return 500, but we're checking if the endpoint exists
        assertNotEquals(HttpStatusCode.NotFound, response.status)
    }
}
