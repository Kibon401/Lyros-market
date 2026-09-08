package com.example.data.repository

import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.OrderLinesTable
import com.example.data.entities.OrdersTable
import com.example.data.entities.ReviewsTable
import com.example.data.entities.UsersTable
import com.example.domain.models.Review
import com.example.domain.repositories.ReviewRepository
import org.jetbrains.exposed.sql.*

class ReviewRepositoryImpl : ReviewRepository {
    override suspend fun addReview(review: Review): Review? = dbQuery {
        try {
            val id = ReviewsTable.insertAndGetId {
                it[userId] = review.userId
                it[productId] = review.productId
                it[rating] = review.rating
                it[comment] = review.comment
            }
            
            ReviewsTable.selectAll().where { ReviewsTable.id eq id }
                .map { rowToReview(it) }
                .singleOrNull()
        } catch (e: Exception) {
            println("DATABASE ERROR in addReview: ${e.message}")
            throw e
        }
    }

    override suspend fun getProductReviews(productId: Int): List<Review> = dbQuery {
        (ReviewsTable innerJoin UsersTable)
            .selectAll().where { ReviewsTable.productId eq productId }
            .map {
                Review(
                    id = it[ReviewsTable.id].value,
                    userId = it[ReviewsTable.userId].value,
                    username = it[UsersTable.username],
                    productId = it[ReviewsTable.productId].value,
                    rating = it[ReviewsTable.rating],
                    comment = it[ReviewsTable.comment],
                    createdAt = it[ReviewsTable.createdAt].toString()
                )
            }
    }

    override suspend fun isUserEligibleToReview(userId: Int, productId: Int): Boolean = dbQuery {
        // Find if there is any order line for this user and product where the order is DELIVERED
        val query = (OrdersTable innerJoin OrderLinesTable)
            .selectAll().where { 
                (OrdersTable.userId eq userId) and 
                (OrderLinesTable.productId eq productId) and 
                (OrdersTable.status eq "DELIVERED") 
            }
        
        val count = query.count()
        println("DEBUG: Eligibility check for User $userId and Product $productId returned $count matches")
        count > 0
    }

    private fun rowToReview(row: ResultRow) = Review(
        id = row[ReviewsTable.id].value,
        userId = row[ReviewsTable.userId].value,
        productId = row[ReviewsTable.productId].value,
        rating = row[ReviewsTable.rating],
        comment = row[ReviewsTable.comment],
        createdAt = row[ReviewsTable.createdAt].toString()
    )
}
