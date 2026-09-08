package com.example.data.repository

import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.UsersTable
import com.example.domain.models.User
import com.example.domain.repositories.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.booleanLiteral // Added import for booleanLiteral

class UserRepositoryImpl : UserRepository {
    override suspend fun findUserByEmail(email: String): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.email eq email and (UsersTable.isActive eq booleanLiteral(true)) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override suspend fun createUser(user: User): User? = dbQuery {
        val insertStatement = UsersTable.insert {
            it[username] = user.username
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[role] = user.role
            it[isActive] = booleanLiteral(true) // Ensure new users are active
        }
        insertStatement.resultedValues?.singleOrNull()?.let { rowToUser(it) }
    }

    override suspend fun findUserById(id: Int): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id and (UsersTable.isActive eq booleanLiteral(true)) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override suspend fun updateUser(user: User) = dbQuery {
        UsersTable.update({ UsersTable.id eq user.id }) {
            it[username] = user.username
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[role] = user.role
            it[isActive] = booleanLiteral(user.isActive)
        }
        Unit
    }

    override suspend fun saveResetCode(email: String, code: String, expiry: LocalDateTime) = dbQuery {
        UsersTable.update({ UsersTable.email eq email }) {
            it[resetCode] = code
            it[resetExpiry] = expiry
        }
        Unit
    }

    override suspend fun verifyResetCode(email: String, code: String): Boolean = dbQuery {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val user = UsersTable.selectAll().where { 
            (UsersTable.email eq email) and 
            (UsersTable.resetCode eq code) and 
            (UsersTable.resetExpiry greater now) and
            (UsersTable.isActive eq booleanLiteral(true)) // Only verify for active users
        }.singleOrNull()
        
        user != null
    }

    override suspend fun resetPassword(email: String, newPasswordHash: String) = dbQuery {
        UsersTable.update({ UsersTable.email eq email }) {
            it[passwordHash] = newPasswordHash
            it[resetCode] = null
            it[resetExpiry] = null
        }
        Unit
    }

    override suspend fun getAllUsers(): List<User> = dbQuery {
        UsersTable.selectAll().map { rowToUser(it) }
    }

    override suspend fun updateUserByAdmin(userId: Int, username: String, email: String, role: String) = dbQuery {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.username] = username
            it[UsersTable.email] = email
            it[UsersTable.role] = role
        }
        Unit
    }

    override suspend fun deleteUser(userId: Int) = dbQuery {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[isActive] = booleanLiteral(false)
        }
        Unit
    }

    private fun rowToUser(row: ResultRow) = User(
        id = row[UsersTable.id].value,
        username = row[UsersTable.username],
        email = row[UsersTable.email],
        passwordHash = row[UsersTable.passwordHash],
        role = row[UsersTable.role],
        isActive = row[UsersTable.isActive]
    )
}
