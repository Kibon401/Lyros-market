package com.example.data.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UsersTable : IntIdTable("site_user") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("USER")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val isActive = bool("is_active").default(true) // New field for soft delete
    
    // Password Reset Fields
    val resetCode = varchar("reset_code", 10).nullable()
    val resetExpiry = datetime("reset_expiry").nullable()
}