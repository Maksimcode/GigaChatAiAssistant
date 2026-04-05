package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeDark
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeLight
import com.example.gigachataiassistant.ui.theme.AppAuthInputLabelDark
import com.example.gigachataiassistant.ui.theme.AppAuthInputLabelLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthGroupedFieldCornerRadius
import com.example.gigachataiassistant.ui.theme.AuthInputFieldHeight
import com.example.gigachataiassistant.ui.theme.AuthInputLabelTextStyle
import com.example.gigachataiassistant.ui.theme.AuthInputStrokeWidth
import com.example.gigachataiassistant.ui.theme.ThemeAlpha

@Composable
fun AuthGroupedCredentialFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val strokeColor = if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
        AppAuthGroupedFieldStrokeDark
    } else {
        AppAuthGroupedFieldStrokeLight
    }
    val labelColor = if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
        AppAuthInputLabelDark
    } else {
        AppAuthInputLabelLight
    }
    val shapeTop = RoundedCornerShape(
        topStart = AuthGroupedFieldCornerRadius,
        topEnd = AuthGroupedFieldCornerRadius,
    )
    val shapeBottom = RoundedCornerShape(
        bottomStart = AuthGroupedFieldCornerRadius,
        bottomEnd = AuthGroupedFieldCornerRadius,
    )
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        errorBorderColor = Color.Transparent,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        unfocusedPlaceholderColor = labelColor,
        focusedPlaceholderColor = labelColor,
        disabledPlaceholderColor = labelColor.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AuthGroupedFieldCornerRadius),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(AuthInputStrokeWidth, strokeColor),
    ) {
        Column {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AuthInputFieldHeight),
                placeholder = {
                    Text(
                        text = stringResource(R.string.auth_email_label),
                        style = AuthInputLabelTextStyle,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = enabled,
                shape = shapeTop,
                colors = fieldColors,
            )
            HorizontalDivider(
                thickness = AuthInputStrokeWidth,
                color = strokeColor,
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = AuthInputFieldHeight),
                placeholder = {
                    Text(
                        text = stringResource(R.string.auth_password_label),
                        style = AuthInputLabelTextStyle,
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = enabled,
                shape = shapeBottom,
                colors = fieldColors,
            )
        }
    }
}
