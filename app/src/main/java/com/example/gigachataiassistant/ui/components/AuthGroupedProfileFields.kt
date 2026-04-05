package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeDark
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeLight
import com.example.gigachataiassistant.ui.theme.AppAuthInputLabelDark
import com.example.gigachataiassistant.ui.theme.AppAuthInputLabelLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthGroupedFieldCornerRadius
import com.example.gigachataiassistant.ui.theme.AuthInputFieldHeight
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatListCardSingleLineVerticalPadding
import com.example.gigachataiassistant.ui.theme.CornerRadiusNone
import com.example.gigachataiassistant.ui.theme.AuthInputLabelTextStyle
import com.example.gigachataiassistant.ui.theme.AuthInputStrokeWidth
import com.example.gigachataiassistant.ui.theme.ThemeAlpha

@Composable
fun AuthGroupedProfileFields(
    name: String,
    onNameChange: (String) -> Unit,
    nameEditable: Boolean,
    nameEnabled: Boolean,
    email: String,
    phone: String?,
    balanceContent: @Composable () -> Unit,
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

    val rows = buildList {
        add(ProfileGroupedRow.Name)
        add(ProfileGroupedRow.Email)
        if (phone != null) add(ProfileGroupedRow.Phone)
        add(ProfileGroupedRow.Balance)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AuthGroupedFieldCornerRadius),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(AuthInputStrokeWidth, strokeColor),
    ) {
        Column {
            rows.forEachIndexed { index, row ->
                val shape = groupedRowShape(index, rows.size)
                when (row) {
                    ProfileGroupedRow.Name -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = onNameChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = AuthInputFieldHeight),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.profile_name_label),
                                    style = AuthInputLabelTextStyle,
                                )
                            },
                            singleLine = true,
                            readOnly = !nameEditable,
                            enabled = nameEnabled,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                            ),
                            shape = shape,
                            colors = fieldColors,
                        )
                    }
                    ProfileGroupedRow.Email -> {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = AuthInputFieldHeight),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.profile_email_label),
                                    style = AuthInputLabelTextStyle,
                                )
                            },
                            singleLine = true,
                            readOnly = true,
                            enabled = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = shape,
                            colors = fieldColors,
                        )
                    }
                    ProfileGroupedRow.Phone -> {
                        OutlinedTextField(
                            value = phone!!,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = AuthInputFieldHeight),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.profile_phone_label),
                                    style = AuthInputLabelTextStyle,
                                )
                            },
                            singleLine = true,
                            readOnly = true,
                            enabled = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = shape,
                            colors = fieldColors,
                        )
                    }
                    ProfileGroupedRow.Balance -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = AuthInputFieldHeight)
                                .clip(shape)
                                .padding(
                                    horizontal = AuthScreenHorizontalPadding,
                                    vertical = ChatListCardSingleLineVerticalPadding,
                                ),
                        ) {
                            balanceContent()
                        }
                    }
                }
                if (index < rows.lastIndex) {
                    HorizontalDivider(
                        thickness = AuthInputStrokeWidth,
                        color = strokeColor,
                    )
                }
            }
        }
    }
}

private enum class ProfileGroupedRow {
    Name,
    Email,
    Phone,
    Balance,
}

private fun groupedRowShape(index: Int, total: Int): RoundedCornerShape {
    val r = AuthGroupedFieldCornerRadius
    return when {
        total == 1 -> RoundedCornerShape(r)
        index == 0 -> RoundedCornerShape(topStart = r, topEnd = r)
        index == total - 1 -> RoundedCornerShape(bottomStart = r, bottomEnd = r)
        else -> RoundedCornerShape(CornerRadiusNone)
    }
}
