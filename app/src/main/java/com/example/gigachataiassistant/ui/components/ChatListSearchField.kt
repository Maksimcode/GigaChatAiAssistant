package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeDark
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthGroupedFieldCornerRadius
import com.example.gigachataiassistant.ui.theme.AuthInputStrokeWidth
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconDark
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconLight
import com.example.gigachataiassistant.ui.theme.ChatListSearchFieldHeight
import com.example.gigachataiassistant.ui.theme.ChatListSearchFieldIconRowSpacing
import com.example.gigachataiassistant.ui.theme.ChatListSearchFieldStyle
import com.example.gigachataiassistant.ui.theme.ChatListSearchPlaceholder
import com.example.gigachataiassistant.ui.theme.ChatListSearchTextDark
import com.example.gigachataiassistant.ui.theme.ChatListSearchTextLight
import com.example.gigachataiassistant.ui.theme.NavDrawerItemHorizontalPadding

@Composable
fun ChatListSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    placeholder: String,
    searchIconContentDescription: String,
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val strokeColor = if (isDark) AppAuthGroupedFieldStrokeDark else AppAuthGroupedFieldStrokeLight
    val textColor = if (isDark) ChatListSearchTextDark else ChatListSearchTextLight
    val iconTint = if (isDark) ChatListAccentIconDark else ChatListAccentIconLight
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(ChatListSearchFieldHeight),
        shape = RoundedCornerShape(AuthGroupedFieldCornerRadius),
        border = BorderStroke(AuthInputStrokeWidth, strokeColor),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NavDrawerItemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ChatListSearchFieldIconRowSpacing),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = ChatListSearchFieldStyle.copy(color = textColor),
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(textColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchClick()
                        focusManager.clearFocus()
                    },
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = ChatListSearchFieldStyle,
                                color = ChatListSearchPlaceholder,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            IconButton(
                onClick = {
                    onSearchClick()
                    focusManager.clearFocus()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = searchIconContentDescription,
                    tint = iconTint,
                )
            }
        }
    }
}
