package com.example.gigachataiassistant

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.data.settings.SettingsRepository
import com.example.gigachataiassistant.navigation.AppNavHost
import com.example.gigachataiassistant.ui.theme.GigaChatAiAssistantTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GigaChatApp() {
    val context = LocalContext.current
    val settingsRepository = remember { SettingsRepository(context.applicationContext) }
    val appTheme by settingsRepository.themeFlow.collectAsState(initial = AppTheme.SYSTEM)

    val isDarkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    GigaChatAiAssistantTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppNavHost()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GigaChatAppPreview() {
    GigaChatApp()
}