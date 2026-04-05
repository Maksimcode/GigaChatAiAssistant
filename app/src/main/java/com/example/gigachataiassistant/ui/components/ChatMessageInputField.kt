package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeDark
import com.example.gigachataiassistant.ui.theme.AppAuthGroupedFieldStrokeLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthGroupedFieldCornerRadius
import com.example.gigachataiassistant.ui.theme.AuthInputStrokeWidth
import com.example.gigachataiassistant.ui.theme.ChatListSearchFieldStyle
import com.example.gigachataiassistant.ui.theme.ChatListSearchPlaceholder
import com.example.gigachataiassistant.ui.theme.ChatListSearchTextDark
import com.example.gigachataiassistant.ui.theme.ChatListSearchTextLight
import com.example.gigachataiassistant.ui.theme.ChatMessageInputContentPaddingVertical
import com.example.gigachataiassistant.ui.theme.ChatMessageInputFieldHeight
import com.example.gigachataiassistant.ui.theme.NavDrawerItemHorizontalPadding

private const val CHAT_INPUT_MAX_LINES = 6

@Composable
fun ChatMessageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Send,
    onImeAction: () -> Unit = {},
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val strokeColor = if (isDark) AppAuthGroupedFieldStrokeDark else AppAuthGroupedFieldStrokeLight
    val textColor = if (isDark) ChatListSearchTextDark else ChatListSearchTextLight
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ChatMessageInputFieldHeight),
        shape = RoundedCornerShape(AuthGroupedFieldCornerRadius),
        border = BorderStroke(AuthInputStrokeWidth, strokeColor),
        color = MaterialTheme.colorScheme.background,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            textStyle = ChatListSearchFieldStyle.copy(color = textColor),
            singleLine = false,
            maxLines = CHAT_INPUT_MAX_LINES,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(textColor),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onSend = { onImeAction() },
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = ChatMessageInputFieldHeight)
                        .padding(
                            horizontal = NavDrawerItemHorizontalPadding,
                            vertical = ChatMessageInputContentPaddingVertical,
                        ),
                    contentAlignment = if (value.isEmpty()) {
                        Alignment.CenterStart
                    } else {
                        Alignment.TopStart
                    },
                ) {
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
    }
}
