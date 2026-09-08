package com.example.data.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MpesaPaymentsTable : IntIdTable("mpesa_payment") {
    val orderId = reference("order_id", OrdersTable)
    val merchantRequestId = varchar("merchant_request_id", 100).uniqueIndex()
    val checkoutRequestId = varchar("checkout_request_id", 100).uniqueIndex()
    val amount = double("amount")
    val phoneNumber = varchar("phone_number", 15)
    val mpesaReceiptNumber = varchar("mpesa_receipt_number", 50).nullable()
    val transactionDate = datetime("transaction_date").nullable()
    val status = varchar("status", 20).default("PENDING") // PENDING, SUCCESS, FAILED
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
