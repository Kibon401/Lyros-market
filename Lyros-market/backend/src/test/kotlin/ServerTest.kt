package com.example

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.*
import io.ktor.server.application.Application // Import Application

class ServerTest {

    @Test
    fun `test root endpoint`() = testApplication {
        // Load the application module
        application {
            module()
        }
        // verify server root returns 200
        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

}