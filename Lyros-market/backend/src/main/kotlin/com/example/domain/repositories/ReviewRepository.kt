package com.example.domain.repositories

import com.example.domain.models.Review

interface ReviewRepository {
    suspend fun addReview(review: Review): Review?
    suspend fun getProductReviews(productId: Int): List<Review>
    suspend fun isUserEligibleToReview(userId: Int, productId: Int): Boolean
}
