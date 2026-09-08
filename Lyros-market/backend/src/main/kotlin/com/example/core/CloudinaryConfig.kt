package com.example.core

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

object CloudinaryConfig {

    // Read credentials from environment variables
    private val cloudName = System.getenv("CLOUDINARY_CLOUD_NAME")
    private val apiKey = System.getenv("CLOUDINARY_API_KEY")
    private val apiSecret = System.getenv("CLOUDINARY_API_SECRET")

    // Lazily initialize the Cloudinary client.
    // This ensures it's only created once when it's first needed.
    val cloudinary: Cloudinary by lazy {
        if (cloudName == null || apiKey == null || apiSecret == null) {
            throw IllegalStateException("Cloudinary credentials are not configured in environment variables.")
        }
        
        Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ))
    }
}
