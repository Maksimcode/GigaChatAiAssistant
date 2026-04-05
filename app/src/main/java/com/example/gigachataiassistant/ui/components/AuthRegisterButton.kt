package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonHeight
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonTextStyle
import com.example.gigachataiassistant.ui.theme.AuthRegisterButtonCornerRadius
import com.example.gigachataiassistant.ui.theme.ThemeAlpha

@Composable
fun AuthRegisterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(AuthPrimaryButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(AuthRegisterButtonCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
            disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
        ),
    ) {
        Text(
            text = text,
            style = AuthPrimaryButtonTextStyle,
            color = MaterialTheme.colorScheme.onSecondary,
        )
    }
}
