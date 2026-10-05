package com.lyrosmarket.app.core

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import org.json.JSONObject
import java.io.IOException

/**
 * A centralized error handler that parses network and HTTP exceptions to provide
 * user-friendly error messages.
 */
suspend fun <T> handleNetworkException(e: Exception): Resource<T> {
    return when (e) {
        is ResponseException -> {
            var errorBody = ""
            try {
                errorBody = e.response.bodyAsText()
                val json = JSONObject(errorBody)
                // Attempt to read the message from common backend error fields
                val message = json.optString("message", json.optString("error", "An unexpected server error occurred"))
                Resource.Error(message)
            } catch (_: Exception) {
                // If it's not JSON or parsing fails, check if it's a plain text message
                if (errorBody.isNotBlank() && !errorBody.contains("<html", ignoreCase = true) && errorBody.length < 150) {
                    Resource.Error(errorBody)
                } else {
                    Resource.Error("Server error: ${e.response.status.value} - ${e.response.status.description}")
                }
            }
        }
        is IOException -> {
            Resource.Error("Network error: Please check your internet connection and try again.")
        }
        else -> {
            Resource.Error(e.message ?: "An unknown error occurred. Please try again.")
        }
    }
}
