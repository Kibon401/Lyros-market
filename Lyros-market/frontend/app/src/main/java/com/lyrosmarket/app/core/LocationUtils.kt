package com.lyrosmarket.app.core

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationUtils {

    suspend fun getAddressFromCoordinates(context: Context, lat: Double, lng: Double): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val fullLine = addr.getAddressLine(0)
                if (!fullLine.isNullOrBlank()) {
                    return@withContext fullLine
                }
                val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: ""
                val feature = addr.featureName ?: ""
                val result = if (feature.isNotBlank() && locality.isNotBlank()) "$feature, $locality" else feature.ifBlank { locality }
                if (result.isNotBlank()) return@withContext result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext "Eldoret, Kenya"
    }
}
