package com.example.presentation.routes

import com.example.domain.models.Review
import com.example.domain.repositories.ReviewRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AddReviewRequest(val productId: Int, val rating: Int, val comment: String)

fun Route.reviewRoutes(reviewRepository: ReviewRepository) {
    get("/products/{productId}/reviews") {
        val productId = call.parameters["productId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val reviews = reviewRepository.getProductReviews(productId)
        call.respond(reviews)
    }

    authenticate {
        post("/reviews/add") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<AddReviewRequest>()
                
                println("DEBUG: PostReview -> User $userId, Product ${request.productId}")

                // 1. Check eligibility
                val isEligible = reviewRepository.isUserEligibleToReview(userId, request.productId)
                
                if (!isEligible) {
                    call.respond(
                        HttpStatusCode.Forbidden, 
                        "You can only review products that have been delivered to you. (User $userId, Product ${request.productId})"
                    )
                    return@post
                }
                
                // 2. Add the review
                val review = Review(
                    userId = userId,
                    productId = request.productId,
                    rating = request.rating,
                    comment = request.comment
                )
                
                val addedReview = reviewRepository.addReview(review)
                if (addedReview != null) {
                    call.respond(HttpStatusCode.Created, addedReview)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error: Could not save review to database.")
                }
            } catch (e: Exception) {
                println("DEBUG: CRITICAL Review Error: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Server Error: ${e.message}")
            }
        }
    }
}
