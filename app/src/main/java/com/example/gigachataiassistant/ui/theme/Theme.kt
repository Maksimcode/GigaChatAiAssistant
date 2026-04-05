package com.example.gigachataiassistant.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryDark,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryDark,
    onPrimaryContainer = AppOnPrimary,
    background = AppBackgroundDark,
    surface = AppBackgroundDark,
    onBackground = AppOnBackgroundDark,
    onSurface = AppOnBackgroundDark,
    secondary = AppAuthSecondaryButtonBackgroundDark,
    onSecondary = AppAuthSecondaryButtonTextDark,
    secondaryContainer = AppAuthSecondaryButtonBackgroundDark,
    onSecondaryContainer = AppAuthSecondaryButtonTextDark,
    tertiary = AppPrimaryDark,
    onTertiary = AppOnPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimaryLight,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryLight,
    onPrimaryContainer = AppOnPrimary,
    background = AppBackgroundLight,
    surface = AppBackgroundLight,
    onBackground = AppOnBackgroundLight,
    onSurface = AppOnBackgroundLight,
    secondary = AppAuthSecondaryButtonBackgroundLight,
    onSecondary = AppAuthSecondaryButtonTextLight,
    secondaryContainer = AppAuthSecondaryButtonBackgroundLight,
    onSecondaryContainer = AppAuthSecondaryButtonTextLight,
    tertiary = AppPrimaryLight,
    onTertiary = AppOnPrimary,
)

@Composable
fun GigaChatAiAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
