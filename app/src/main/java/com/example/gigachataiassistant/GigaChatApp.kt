package com.example.gigachataiassistant

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gigachataiassistant.navigation.AppNavHost
import com.example.gigachataiassistant.ui.theme.GigaChatAiAssistantTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GigaChatApp() {
    GigaChatAiAssistantTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GigaChatAppPreview() {
    GigaChatApp()
}