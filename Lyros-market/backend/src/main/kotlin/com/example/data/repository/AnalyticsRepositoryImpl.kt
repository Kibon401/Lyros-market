package com.example.data.repository

import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.OrderLinesTable
import com.example.data.entities.OrdersTable
import com.example.data.entities.ProductsTable
import com.example.data.entities.UsersTable
import com.example.domain.models.Analytics
import com.example.domain.models.TopProduct
import com.example.domain.models.UserGrowth
import com.example.domain.repositories.AnalyticsRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.date

class AnalyticsRepositoryImpl : AnalyticsRepository {
    override suspend fun getDashboardStats(): Analytics = dbQuery {
        val totalRevenueSum = OrdersTable.totalAmount.sum()
        val totalRevenue = OrdersTable
            .select(totalRevenueSum)
            .where { OrdersTable.status eq "DELIVERED" }
            .map { it[totalRevenueSum] }
            .singleOrNull() ?: 0.0

        val totalOrders = OrdersTable.selectAll().count().toInt()
        val totalUsers = UsersTable.selectAll().count().toInt()

        val quantitySum = OrderLinesTable.quantity.sum()
        
        val topProducts = (OrderLinesTable innerJoin ProductsTable)
            .select(
                ProductsTable.id,
                ProductsTable.name,
                quantitySum
            )
            .groupBy(ProductsTable.id, ProductsTable.name)
            .orderBy(quantitySum, SortOrder.DESC)
            .limit(5)
            .map {
                val prodId = it[ProductsTable.id].value
                val sold = it[quantitySum] ?: 0
                
                // Calculate revenue for this product by summing quantity * price for its order lines
                // Since we are inside dbQuery, we can run another query safely
                val revenue = OrderLinesTable
                    .selectAll().where { OrderLinesTable.productId eq prodId }
                    .sumOf { line -> line[OrderLinesTable.quantity] * line[OrderLinesTable.price] }

                TopProduct(
                    productId = prodId,
                    productName = it[ProductsTable.name],
                    totalSold = sold,
                    revenue = revenue
                )
            }

        val userDate = UsersTable.createdAt.date()
        val userCount = UsersTable.id.count()
        val userGrowth = UsersTable
            .select(userDate, userCount)
            .groupBy(userDate)
            .orderBy(userDate, SortOrder.ASC)
            .map {
                UserGrowth(
                    date = it[userDate].toString(),
                    count = it[userCount].toInt()
                )
            }

        Analytics(
            totalRevenue = totalRevenue,
            totalOrders = totalOrders,
            totalUsers = totalUsers,
            topProducts = topProducts,
            userGrowth = userGrowth
        )
    }
}
