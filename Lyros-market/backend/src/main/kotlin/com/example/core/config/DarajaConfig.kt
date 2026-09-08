package com.example.core.config

import io.github.cdimascio.dotenv.dotenv

object DarajaConfig {
    // This will load variables from your .env file for local development
    private val dotenv = dotenv {
        directory = "./"
        ignoreIfMissing = true // In production, we expect variables to be in the environment
    }

    // Read credentials from the environment.
    // This works for both local development (via .env) and production (via Render's dashboard).
    val CONSUMER_KEY: String = System.getenv("MPESA_CONSUMER_KEY") ?: dotenv["MPESA_CONSUMER_KEY"]
    val CONSUMER_SECRET: String = System.getenv("MPESA_CONSUMER_SECRET") ?: dotenv["MPESA_CONSUMER_SECRET"]
    val BUSINESS_SHORT_CODE: String = System.getenv("MPESA_BUSINESS_SHORT_CODE") ?: dotenv["MPESA_BUSINESS_SHORT_CODE"]
    val PASSKEY: String = System.getenv("MPESA_PASSKEY") ?: dotenv["MPESA_PASSKEY"]
    val CALLBACK_URL: String = System.getenv("MPESA_CALLBACK_URL") ?: dotenv["MPESA_CALLBACK_URL"]

    // These URLs are constant and can remain here.
    const val AUTH_URL = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials"
    const val STK_PUSH_URL = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest"
}
