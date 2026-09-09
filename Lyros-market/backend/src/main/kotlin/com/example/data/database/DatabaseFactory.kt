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
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        } else {
            val dbHost = System.getenv("DB_HOST")
            val dbPort = System.getenv("DB_PORT")
            val dbName = System.getenv("DB_NAME")
            val dbUser = System.getenv("DB_USER")
            val dbPassword = System.getenv("DB_PASSWORD")

            val driverClassName = "org.postgresql.Driver"
            val jdbcUrl = "jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME"

            println("Connecting to Supabase (PostgreSQL) database...")
            Database.connect(createHikariDataSource(jdbcUrl, driverClassName, dbUser, dbPassword))
        }

        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable, CategoriesTable, ProductsTable, OrdersTable, OrderLinesTable,
                CartItemsTable, ReviewsTable, MpesaPaymentsTable, DriverLocationsTable
            )
        }

        if (!isTest) {
            runBlocking {
                val categoryCount = dbQuery { CategoriesTable.selectAll().count() }
                if (categoryCount == 0L) {
                    println("No categories found. Seeding initial data...")
                    SeedData.seed(ProductRepositoryImpl())
                } else {
                    println("Categories already exist. Skipping seeding.")
                }
            }
        }
    }

    private fun createHikariDataSource(url: String, driver: String, user: String, pass: String) =
        HikariDataSource(HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            password = pass
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}