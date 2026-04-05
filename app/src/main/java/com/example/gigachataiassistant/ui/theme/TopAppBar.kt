package com.example.gigachataiassistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun topAppBarContentColor(): Color =
    if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
        AppTopBarTitleDark
    } else {
        AppTopBarTitleLight
    }
