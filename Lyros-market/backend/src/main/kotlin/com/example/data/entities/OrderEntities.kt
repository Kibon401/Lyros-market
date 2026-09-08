package com.example.data.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object OrdersTable : IntIdTable("shop_order") {
    val userId = reference("user_id", UsersTable)
    val driverId = reference("driver_id", UsersTable).nullable() // The driver assigned to this order
    val orderDate = datetime("order_date").defaultExpression(CurrentDateTime)
    val subtotal = double("subtotal")
    val deliveryFee = double("delivery_fee").default(0.0)
    val totalAmount = double("total_amount")
    val status = varchar("status", 20).default("PENDING") // PENDING, PAID, ACCEPTED, PICKED_UP, DELIVERED, CANCELLED
    val deliveryAddress = text("delivery_address")
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
}

object OrderLinesTable : IntIdTable("order_line") {
    val orderId = reference("order_id", OrdersTable)
    val productId = reference("product_id", ProductsTable)
    val quantity = integer("quantity")
    val price = double("price")
}

object CartItemsTable : IntIdTable("shopping_cart_item") {
    val userId = reference("user_id", UsersTable)
    val productId = reference("product_id", ProductsTable)
    val quantity = integer("quantity")
}
