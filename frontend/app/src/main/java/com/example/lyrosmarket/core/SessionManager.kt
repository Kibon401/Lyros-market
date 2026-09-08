package com.example.lyrosmarket.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var prefs: SharedPreferences? = null

    init {
        initPrefs()
    }

    private fun initPrefs() {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            prefs = EncryptedSharedPreferences.create(
                context,
                "lyros_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("SessionManager", "Initialization failed, clearing corrupted data", e)
            // If initialization fails (common Keystore issue), clear the file and try again
            try {
                context.getSharedPreferences("lyros_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                // On some devices, even delete is needed
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                prefs = EncryptedSharedPreferences.create(
                    context,
                    "lyros_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e2: Exception) {
                Log.e("SessionManager", "Critical failure in session recovery", e2)
                // Fallback to standard prefs if encryption is completely broken on this device/state
                prefs = context.getSharedPreferences("lyros_prefs_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    fun saveToken(token: String) {
        prefs?.edit()?.putString("auth_token", token)?.apply()
    }

    fun getToken(): String? {
        return prefs?.getString("auth_token", null)
    }

    fun saveRefreshToken(token: String) {
        prefs?.edit()?.putString("refresh_token", token)?.apply()
    }

    fun getRefreshToken(): String? {
        return prefs?.getString("refresh_token", null)
    }

    fun saveUserEmail(email: String) {
        prefs?.edit()?.putString("user_email", email)?.apply()
    }

    fun getUserEmail(): String? {
        return prefs?.getString("user_email", null)
    }

    fun saveUserName(name: String) {
        prefs?.edit()?.putString("user_name", name)?.apply()
    }

    fun getUserName(): String? {
        return prefs?.getString("user_name", null)
    }

    fun clearSession() {
        prefs?.edit()?.clear()?.apply()
    }
}
