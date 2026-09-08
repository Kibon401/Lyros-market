package com.example.data.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ReviewsTable : IntIdTable("user_review") {
    val userId = reference("user_id", UsersTable)
    val productId = reference("product_id", ProductsTable)
    val rating = integer("rating")
    val comment = text("comment")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
