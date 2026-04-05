package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonHeight
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonTextStyle
import com.example.gigachataiassistant.ui.theme.CircularProgressStrokeWidth
import com.example.gigachataiassistant.ui.theme.StandardIconSize
import com.example.gigachataiassistant.ui.theme.ThemeAlpha

@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AuthPrimaryButtonHeight),
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(StandardIconSize),
                strokeWidth = CircularProgressStrokeWidth,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = text,
                style = AuthPrimaryButtonTextStyle,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
