package com.example.core.utils

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailService {
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"
    private const val SENDER_EMAIL = "jnyangara303@gmail.com" // Placeholder
    private const val SENDER_PASSWORD = "gnkc sdiq ohoq icxv" // Placeholder

    fun sendResetEmail(recipient: String, resetCode: String) {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", SMTP_HOST)
            put("mail.smtp.port", SMTP_PORT)
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
                subject = "Lyros Market - Password Reset"
                setText("Your password reset code is: $resetCode\n\nThis code will expire in 15 minutes.")
            }

            Transport.send(message) // Commented out to avoid crashes without real credentials
            println("DEBUG: Email sent to $recipient with code $resetCode")
        } catch (e: Exception) {
            println("ERROR: Failed to send email: ${e.message}")
        }
    }
}
