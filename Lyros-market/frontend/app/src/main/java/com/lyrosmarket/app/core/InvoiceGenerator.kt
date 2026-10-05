package com.lyrosmarket.app.core

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.lyrosmarket.app.domain.model.Order
import java.io.File
import java.io.FileOutputStream

object InvoiceGenerator {

    fun generateAndOpenInvoice(context: Context, order: Order) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions in points
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val primaryPaint = Paint().apply {
                color = Color.parseColor("#2D5A27")
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val titlePaint = Paint().apply {
                color = Color.parseColor("#1C1B1F")
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val textPaint = Paint().apply {
                color = Color.parseColor("#333333")
                textSize = 12f
                isAntiAlias = true
            }

            val boldPaint = Paint().apply {
                color = Color.parseColor("#1C1B1F")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val linePaint = Paint().apply {
                color = Color.parseColor("#CCCCCC")
                strokeWidth = 1f
            }

            var y = 50f

            // App Header
            canvas.drawText("LYROS MARKET", 40f, y, primaryPaint)
            y += 20f
            textPaint.textSize = 10f
            canvas.drawText("Fresh Produce & Quality Finds", 40f, y, textPaint)
            
            // Document Title
            canvas.drawText("INVOICE / RECEIPT", 400f, 50f, titlePaint)
            textPaint.textSize = 11f
            val formattedDate = if (order.date.length >= 10) order.date.take(10) else order.date
            canvas.drawText("Date: $formattedDate", 400f, 70f, textPaint)
            canvas.drawText("Order #: ${order.id}", 400f, 85f, textPaint)
            canvas.drawText("Status: ${order.status.uppercase()}", 400f, 100f, textPaint)

            y = 120f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 25f

            // Shipping / Customer Info
            canvas.drawText("Delivery Address:", 40f, y, boldPaint)
            y += 15f
            canvas.drawText(order.shippingAddress.ifBlank { "Standard Delivery Address" }, 40f, y, textPaint)
            
            y += 35f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 20f

            // Table Header
            canvas.drawText("ITEM DESCRIPTION", 40f, y, boldPaint)
            canvas.drawText("QTY", 320f, y, boldPaint)
            canvas.drawText("PRICE", 390f, y, boldPaint)
            canvas.drawText("TOTAL", 480f, y, boldPaint)

            y += 10f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 20f

            // Items List
            for (item in order.items) {
                val itemTotal = item.price * item.quantity
                val name = if (item.productName.length > 35) item.productName.take(32) + "..." else item.productName
                
                canvas.drawText(name, 40f, y, textPaint)
                canvas.drawText("${item.quantity}", 320f, y, textPaint)
                canvas.drawText("KES ${item.price.toInt()}", 390f, y, textPaint)
                canvas.drawText("KES ${itemTotal.toInt()}", 480f, y, textPaint)
                
                y += 20f
                if (y > 720f) break
            }

            y += 10f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 25f

            // Summary
            val subtotal = order.totalAmount - order.shippingFee
            canvas.drawText("Subtotal:", 390f, y, textPaint)
            canvas.drawText("KES ${subtotal.toInt()}", 480f, y, textPaint)
            y += 18f

            canvas.drawText("Shipping Fee:", 390f, y, textPaint)
            canvas.drawText("KES ${order.shippingFee.toInt()}", 480f, y, textPaint)
            y += 22f

            boldPaint.textSize = 14f
            primaryPaint.textSize = 14f
            canvas.drawText("Grand Total:", 390f, y, boldPaint)
            canvas.drawText("KES ${order.totalAmount.toInt()}", 480f, y, primaryPaint)

            // Footer
            y = 780f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 20f
            textPaint.textSize = 10f
            canvas.drawText("Thank you for choosing Lyros Market! For inquiries, email support@lyrosmarket.com", 40f, y, textPaint)

            pdfDocument.finishPage(page)

            // Save PDF File
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "LyrosMarket_Invoice_${order.id}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            // Open or Share PDF File using FileProvider
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(viewIntent, "Open Invoice PDF")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (viewIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooserIntent)
            } else {
                Toast.makeText(context, "Invoice saved to ${file.name}", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to generate invoice: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
