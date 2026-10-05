package com.lyrosmarket.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lyrosmarket.app.ui.theme.LyrosMarketTheme
import com.lyrosmarket.app.core.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

import com.lyrosmarket.app.presentation.Navigation

import android.content.Context
import org.osmdroid.config.Configuration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize osmdroid configuration globally before any Compose UI is loaded
        val prefs = applicationContext.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        Configuration.getInstance().load(applicationContext, prefs)
        // Use a highly unique user agent without 'com.example' (OSM blocks example packages)
        Configuration.getInstance().userAgentValue = "LyrosMarketAndroidApp/1.0 (contact@lyrosmarket.com)"
        
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            val savedDarkMode by sessionManager.darkModeFlow.collectAsStateWithLifecycle(
                initialValue = sessionManager.isDarkMode()
            )
            val isDark = savedDarkMode ?: systemDark

            LyrosMarketTheme(darkTheme = isDark) {
                Navigation()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LyrosMarketTheme {
        Greeting("Android")
    }
}
