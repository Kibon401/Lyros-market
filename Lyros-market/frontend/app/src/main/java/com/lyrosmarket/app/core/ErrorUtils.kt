package com.lyrosmarket.app.core

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.json.JSONObject
import java.io.IOException

/**
 * A centralized error handler that parses network and HTTP exceptions to provide
 * user-friendly error messages, specifically handling authentication (401/403)
 * and other common status codes.
 */
suspend fun <T> handleNetworkException(
    e: Exception,
    sessionManager: SessionManager? = null
): Resource<T> {
    return when (e) {
        is ResponseException -> {
            val status = e.response.status
            var errorBody = ""
            var parsedMessage: String? = null

            try {
                errorBody = e.response.bodyAsText()
                if (errorBody.isNotBlank()) {
                    val json = JSONObject(errorBody)
                    parsedMessage = json.optString("message", "")
                        .ifBlank { json.optString("error", "") }
                        .ifBlank { json.optString("detail", "") }
                        .ifBlank { json.optString("description", "") }
                        .ifBlank { json.optString("err", "") }
                }
            } catch (_: Exception) {
                if (errorBody.isNotBlank() && !errorBody.contains("<html", ignoreCase = true) && errorBody.length < 150) {
                    parsedMessage = errorBody.trim()
                }
            }

            if (parsedMessage.isNullOrBlank()) {
                val msg = e.message ?: ""
                if (msg.contains("Text:", ignoreCase = true)) {
                    val textPart = msg.substringAfter("Text:").trim().removeSurrounding("\"").removeSurrounding("'")
                    if (textPart.isNotBlank() && !textPart.contains("<html", ignoreCase = true)) {
                        parsedMessage = textPart
                    }
                } else if (msg.isNotBlank()) {
                    parsedMessage = msg
                }
            }

            // If 401 Unauthorized occurs on protected endpoints, clear session
            if (status == HttpStatusCode.Unauthorized) {
                sessionManager?.clearSession()
            }

            // Determine user-friendly error message
            val finalMessage = when {
                !parsedMessage.isNullOrBlank() -> {
                    if (parsedMessage.contains("invalid email or password", ignoreCase = true) ||
                        parsedMessage.contains("invalid credentials", ignoreCase = true) ||
                        parsedMessage.contains("unauthorized", ignoreCase = true)) {
                        "Invalid email or password"
                    } else {
                        parsedMessage
                    }
                }

                status == HttpStatusCode.Unauthorized -> "Invalid email or password"
                status == HttpStatusCode.Forbidden -> "Access denied: You do not have permission to perform this action."
                status == HttpStatusCode.BadRequest -> "Invalid request: Please check your information and try again."
                status == HttpStatusCode.NotFound -> "The requested item or page was not found."
                status == HttpStatusCode.Conflict -> "Conflict: An account or resource with these details already exists."
                status.value >= 500 -> "Server error (${status.value}): Please try again later."
                else -> "Server error (${status.value}): ${status.description}"
            }

            Resource.Error(finalMessage)
        }
        is IOException -> {
            Resource.Error("Network error: Please check your internet connection and try again.")
        }
        else -> {
            val msg = e.message ?: ""
            val cleanMsg = when {
                msg.contains("invalid email or password", ignoreCase = true) ||
                msg.contains("invalid credentials", ignoreCase = true) ||
                msg.contains("401", ignoreCase = true) -> "Invalid email or password"
                else -> msg.ifBlank { "An unknown error occurred. Please try again." }
            }
            Resource.Error(cleanMsg)
        }
    }
}
