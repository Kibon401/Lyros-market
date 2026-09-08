package com.example.presentation.routes

import com.cloudinary.utils.ObjectUtils
import com.example.core.CloudinaryConfig
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.uploadRoutes() {
    authenticate {
        route("/upload") {
            // Intercept all routes within this block to check for ADMIN role
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@intercept finish()
                }
            }

            post("/product-image") {
                val multipartData = call.receiveMultipart()
                var imageUrl: String? = null

                multipartData.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val fileBytes = part.streamProvider().readBytes()

                        try {
                            // Use the centralized Cloudinary client
                            val uploadResult = CloudinaryConfig.cloudinary.uploader().upload(
                                fileBytes, 
                                ObjectUtils.emptyMap()
                            )
                            imageUrl = uploadResult["secure_url"] as? String
                        } catch (e: Exception) {
                            application.log.error("Cloudinary upload failed", e)
                            call.respond(HttpStatusCode.InternalServerError, "Image upload failed")
                            return@forEachPart
                        }
                    }
                    part.dispose()
                }

                if (imageUrl != null) {
                    call.respond(HttpStatusCode.OK, mapOf("imageUrl" to imageUrl))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "No valid file uploaded or upload failed")
                }
            }
        }
    }
}
