package com.example.presentation.routes

import com.example.core.config.DarajaConfig
import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.MpesaPaymentsTable
import com.example.data.entities.OrdersTable
import com.example.domain.repositories.OrderRepository
import com.example.presentation.requests.DarajaAuthResponse
import com.example.presentation.requests.DarajaStkResponse
import com.example.presentation.requests.StkPushRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.*
import java.util.Base64
import java.text.SimpleDateFormat

fun Route.paymentRoutes(orderRepository: OrderRepository) {
    val client = HttpClient(OkHttp) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                coerceInputValues = true
            })
        }
    }

    authenticate {
        post("/payments/stk-push") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<StkPushRequest>()
                val order = orderRepository.getOrderById(request.orderId)
                if (order == null || order.userId != userId) return@post call.respond(HttpStatusCode.NotFound)

                if (order.status != "PENDING") {
                    return@post call.respond(HttpStatusCode.BadRequest, "Order is already ${order.status}")
                }

                val credentials = "${DarajaConfig.CONSUMER_KEY}:${DarajaConfig.CONSUMER_SECRET}"
                val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray()).trim()
                
                val authResponse: DarajaAuthResponse = client.get(DarajaConfig.AUTH_URL) {
                    header("Authorization", "Basic $encodedCredentials")
                }.body()

                val timestamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
                val password = Base64.getEncoder().encodeToString("${DarajaConfig.BUSINESS_SHORT_CODE}${DarajaConfig.PASSKEY}$timestamp".toByteArray())
                
                val testAmount = 1

                val stkPushBody = buildJsonObject {
                    put("BusinessShortCode", DarajaConfig.BUSINESS_SHORT_CODE)
                    put("Password", password)
                    put("Timestamp", timestamp)
                    put("TransactionType", "CustomerPayBillOnline")
                    put("Amount", testAmount)
                    put("PartyA", request.phoneNumber)
                    put("PartyB", DarajaConfig.BUSINESS_SHORT_CODE)
                    put("PhoneNumber", request.phoneNumber)
                    put("CallBackURL", DarajaConfig.CALLBACK_URL)
                    put("AccountReference", "Order-${order.id}")
                    put("TransactionDesc", "Payment for order ${order.id}")
                }

                val stkPushResponse = client.post(DarajaConfig.STK_PUSH_URL) {
                    header("Authorization", "Bearer ${authResponse.access_token}")
                    contentType(ContentType.Application.Json)
                    setBody(stkPushBody)
                }

                val responseBodyText = stkPushResponse.bodyAsText()
                println("DEBUG: Raw Daraja STK Push Response: $responseBodyText")

                if (stkPushResponse.status != HttpStatusCode.OK) {
                    return@post call.respond(HttpStatusCode.InternalServerError, "Daraja Error: $responseBodyText")
                }

                val stkData = Json { ignoreUnknownKeys = true }.decodeFromString<DarajaStkResponse>(responseBodyText)

                if (stkData.ResponseCode == "0") {
                    dbQuery {
                        MpesaPaymentsTable.insert {
                            it[orderId] = order.id!!
                            it[merchantRequestId] = stkData.MerchantRequestID
                            it[checkoutRequestId] = stkData.CheckoutRequestID
                            it[amount] = testAmount.toDouble()
                            it[phoneNumber] = request.phoneNumber
                            it[status] = "PENDING"
                        }
                    }
                    call.respond(HttpStatusCode.OK, stkData)
                } else {
                    call.respond(HttpStatusCode.BadRequest, stkData)
                }
            } catch (e: Exception) {
                println("DEBUG: STK Push Exception: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
            }
        }
    }

    post("/payments/callback") {
        try {
            val rawBody = call.receiveText()
            println("CRITICAL DEBUG: Callback Body: $rawBody")
            
            val body = Json.parseToJsonElement(rawBody).jsonObject
            val stkCallback = body["Body"]?.jsonObject?.get("stkCallback")?.jsonObject
            val resultCode = stkCallback?.get("ResultCode")?.jsonPrimitive?.int
            val checkoutRequestId = stkCallback?.get("CheckoutRequestID")?.jsonPrimitive?.content

            if (checkoutRequestId != null) {
                dbQuery {
                    if (resultCode == 0) {
                        val metadata = stkCallback["CallbackMetadata"]?.jsonObject?.get("Item")?.jsonArray
                        
                        // Parse Mpesa Receipt Number
                        val receiptNumber = metadata?.find { it.jsonObject["Name"]?.jsonPrimitive?.content == "MpesaReceiptNumber" }
                            ?.jsonObject?.get("Value")?.jsonPrimitive?.content

                        // Parse Transaction Date
                        val rawDate = metadata?.find { it.jsonObject["Name"]?.jsonPrimitive?.content == "TransactionDate" }
                            ?.jsonObject?.get("Value")?.jsonPrimitive?.content
                        
                        val formattedDate = rawDate?.let {
                            val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                            val date = sdf.parse(it)
                            val calendar = Calendar.getInstance()
                            calendar.time = date
                            java.time.LocalDateTime.of(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1,
                                calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                calendar.get(Calendar.SECOND)
                            ).toKotlinLocalDateTime()
                        }

                        // Update Payment Record with all details
                        MpesaPaymentsTable.update({ MpesaPaymentsTable.checkoutRequestId eq checkoutRequestId }) {
                            it[status] = "SUCCESS"
                            it[mpesaReceiptNumber] = receiptNumber
                            if (formattedDate != null) {
                                it[transactionDate] = formattedDate
                            }
                        }

                        // Update Order to PAID
                        val payment = MpesaPaymentsTable.selectAll().where { MpesaPaymentsTable.checkoutRequestId eq checkoutRequestId }.singleOrNull()
                        payment?.get(MpesaPaymentsTable.orderId)?.value?.let { orderId ->
                            OrdersTable.update({ OrdersTable.id eq orderId }) {
                                it[status] = "PAID"
                            }
                            println("CRITICAL DEBUG: Order $orderId successfully PAID with Receipt $receiptNumber")
                        }
                    } else {
                        MpesaPaymentsTable.update({ MpesaPaymentsTable.checkoutRequestId eq checkoutRequestId }) {
                            it[status] = "FAILED"
                        }
                    }
                }
            }
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            println("CRITICAL DEBUG: Callback Error: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.OK)
        }
    }
}
