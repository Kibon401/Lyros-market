package com.example.data.database

import com.example.data.entities.*
import com.example.data.repository.ProductRepositoryImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(isTest: Boolean = false) {

        val database = if (isTest) {

            Database.connect(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
                driver = "org.h2.Driver"
            )

        } else {

            val databaseUrl = System.getenv("DATABASE_URL")
                ?: error("DATABASE_URL environment variable is not set")

            val dbUser = System.getenv("DB_USER")
                ?: error("DB_USER environment variable is not set")

            val dbPassword = System.getenv("DB_PASSWORD")
                ?: error("DB_PASSWORD environment variable is not set")

            println("Connecting to Supabase PostgreSQL database...")
            println("Database URL: ${databaseUrl.substringBefore("?")}")

            Database.connect(
                createHikariDataSource(
                    databaseUrl,
                    "org.postgresql.Driver",
                    dbUser,
                    dbPassword
                )
            )
        }

        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                CategoriesTable,
                ProductsTable,
                OrdersTable,
                OrderLinesTable,
                CartItemsTable,
                ReviewsTable,
                MpesaPaymentsTable,
                DriverLocationsTable
            )
        }

        if (!isTest) {

            runBlocking {

                val categoryCount =
                    dbQuery {
                        CategoriesTable.selectAll().count()
                    }

                if (categoryCount == 0L) {

                    println("No categories found. Seeding initial data...")

                    SeedData.seed(
                        ProductRepositoryImpl()
                    )

                } else {

                    println("Categories already exist. Skipping seeding.")
                }
            }
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        pass: String
    ): HikariDataSource {

        val config = HikariConfig().apply {

            driverClassName = driver

            jdbcUrl = url

            username = user
            password = pass

            maximumPoolSize = 10
            minimumIdle = 2

            isAutoCommit = false

            transactionIsolation =
                "TRANSACTION_REPEATABLE_READ"

            connectionTimeout = 30_000
            validationTimeout = 5_000

            poolName = "LyrosHikariPool"
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
        block: suspend () -> T
    ): T =
        newSuspendedTransaction(Dispatchers.IO) {
            block()
        }
}