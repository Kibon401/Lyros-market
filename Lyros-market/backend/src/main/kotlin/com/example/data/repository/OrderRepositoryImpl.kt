package com.example.data.repository

import com.example.core.utils.LocationUtils
import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.CartItemsTable
import com.example.data.entities.OrderLinesTable
import com.example.data.entities.OrdersTable
import com.example.data.entities.ProductsTable
import com.example.domain.models.Order
import com.example.domain.models.OrderItem
import com.example.domain.repositories.OrderRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.intLiteral
import java.time.Duration

class OrderRepositoryImpl : OrderRepository {
    override suspend fun createOrder(userId: Int, deliveryAddress: String, latitude: Double?, longitude: Double?): Order? = dbQuery {
        val cartItems = (CartItemsTable innerJoin ProductsTable)
            .selectAll().where { CartItemsTable.userId eq userId }
            .toList()

        if (cartItems.isEmpty()) return@dbQuery null

        // 1. Validate stock availability for all items before proceeding
        for (item in cartItems) {
            val requestedQty = item[CartItemsTable.quantity]
            val availableStock = item[ProductsTable.stockQuantity]
            val productName = item[ProductsTable.name]
            
            if (requestedQty > availableStock) {
                // In a real app, we'd throw a custom exception. 
                // For now, we return null to signal failure.
                println("DEBUG: Insufficient stock for $productName. Requested: $requestedQty, Available: $availableStock")
                return@dbQuery null 
            }
        }

        val subtotal = cartItems.sumOf { it[ProductsTable.price] * it[CartItemsTable.quantity] }
        val deliveryFee = LocationUtils.calculateDeliveryFee(latitude, longitude)
        val totalAmount = subtotal + deliveryFee

        val orderId = OrdersTable.insert {
            it[OrdersTable.userId] = userId
            it[OrdersTable.subtotal] = subtotal
            it[OrdersTable.deliveryFee] = deliveryFee
            it[OrdersTable.totalAmount] = totalAmount
            it[OrdersTable.deliveryAddress] = deliveryAddress
            it[OrdersTable.latitude] = latitude
            it[OrdersTable.longitude] = longitude
            it[OrdersTable.status] = "PENDING"
        }[OrdersTable.id].value

        for (item in cartItems) {
            val prodId = item[CartItemsTable.productId]
            val priceAtTime = item[ProductsTable.price]
            val qty = item[CartItemsTable.quantity]
            val currentStock = item[ProductsTable.stockQuantity]

            OrderLinesTable.insert {
                it[OrderLinesTable.orderId] = orderId
                it[OrderLinesTable.productId] = prodId
                it[OrderLinesTable.quantity] = qty
                it[OrderLinesTable.price] = priceAtTime
            }
            
            ProductsTable.update({ ProductsTable.id eq prodId }) {
                it[stockQuantity] = currentStock - qty
            }
        }

        CartItemsTable.deleteWhere { CartItemsTable.userId eq userId }
        
        val orderRow = OrdersTable.selectAll().where { OrdersTable.id eq orderId }.singleOrNull() 
        if (orderRow == null) return@dbQuery null

        val items = OrderLinesTable.selectAll().where { OrderLinesTable.orderId eq orderId }
            .map {
                OrderItem(
                    id = it[OrderLinesTable.id].value,
                    productId = it[OrderLinesTable.productId].value,
                    quantity = it[OrderLinesTable.quantity],
                    price = it[OrderLinesTable.price]
                )
            }
        
        rowToOrder(orderRow, items)
    }

    override suspend fun getOrderById(orderId: Int): Order? = dbQuery {
        val orderRow = OrdersTable.selectAll().where { OrdersTable.id eq orderId }.singleOrNull() ?: return@dbQuery null
        val items = OrderLinesTable.selectAll().where { OrderLinesTable.orderId eq orderId }
            .map {
                OrderItem(
                    id = it[OrderLinesTable.id].value,
                    productId = it[OrderLinesTable.productId].value,
                    quantity = it[OrderLinesTable.quantity],
                    price = it[OrderLinesTable.price]
                )
            }
        
        rowToOrder(orderRow, items)
    }

    override suspend fun getUserOrders(userId: Int): List<Order> = dbQuery {
        OrdersTable.selectAll().where { OrdersTable.userId eq userId }.map { rowToOrder(it) }
    }

    override suspend fun getAvailableOrders(): List<Order> = dbQuery {
        OrdersTable.selectAll().where { 
            (OrdersTable.status eq "PAID") and (OrdersTable.driverId.isNull()) 
        }.map { rowToOrder(it) }
    }

    override suspend fun getDriverOrders(driverId: Int): List<Order> = dbQuery {
        OrdersTable.selectAll().where { OrdersTable.driverId eq driverId }.map { rowToOrder(it) }
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): Unit = dbQuery {
        OrdersTable.update({ OrdersTable.id eq orderId }) {
            it[OrdersTable.status] = status
        }
        Unit
    }

    override suspend fun assignDriver(orderId: Int, driverId: Int): Unit = dbQuery {
        OrdersTable.update({ OrdersTable.id eq orderId }) {
            it[OrdersTable.driverId] = driverId
            it[OrdersTable.status] = "ACCEPTED"
        }
        Unit
    }

    override suspend fun cancelExpiredPendingOrders(expirationMinutes: Long) = dbQuery {
        val now = Clock.System.now().toJavaInstant()
        val expiredTime = now.minus(Duration.ofMinutes(expirationMinutes))

        val expiredOrders = OrdersTable.selectAll().where {
            (OrdersTable.status eq "PENDING") and (OrdersTable.orderDate less expiredTime)
        }.toList()

        for (orderRow in expiredOrders) {
            val orderId = orderRow[OrdersTable.id].value
            println("DEBUG: Cancelling expired order $orderId")

            // Restock items
            val orderLines = OrderLinesTable.selectAll().where { OrderLinesTable.orderId eq orderId }.toList()
            for (line in orderLines) {
                val productId = line[OrderLinesTable.productId]
                val quantity = line[OrderLinesTable.quantity]
                
                // Corrected syntax for Exposed update with arithmetic expression
                ProductsTable.update({ ProductsTable.id eq productId }) {
                    with(SqlExpressionBuilder) {
                        it[stockQuantity] = stockQuantity + intLiteral(quantity)
                    }
                }
                println("DEBUG: Restocked $quantity of product $productId for order $orderId")
            }

            // Update order status to CANCELLED
            OrdersTable.update({ OrdersTable.id eq orderId }) {
                it[status] = "CANCELLED"
            }
            println("DEBUG: Order $orderId status updated to CANCELLED")
        }
    }

    override suspend fun getAllOrders(): List<Order> = dbQuery {
        OrdersTable.selectAll().map { rowToOrder(it) }
    }

    private fun rowToOrder(row: ResultRow, items: List<OrderItem> = emptyList()) = Order(
        id = row[OrdersTable.id].value,
        userId = row[OrdersTable.userId].value,
        driverId = row[OrdersTable.driverId]?.value,
        orderDate = row[OrdersTable.orderDate].toString(),
        subtotal = row[OrdersTable.subtotal],
        deliveryFee = row[OrdersTable.deliveryFee],
        totalAmount = row[OrdersTable.totalAmount],
        status = row[OrdersTable.status],
        deliveryAddress = row[OrdersTable.deliveryAddress],
        latitude = row[OrdersTable.latitude],
        longitude = row[OrdersTable.longitude],
        items = items
    )
}
