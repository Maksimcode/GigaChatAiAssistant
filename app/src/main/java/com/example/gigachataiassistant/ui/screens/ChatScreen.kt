package com.example.gigachataiassistant.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.local.MessageEntity
import com.example.gigachataiassistant.data.local.MessageRole
import com.example.gigachataiassistant.ui.chat.ChatViewModel
import com.example.gigachataiassistant.ui.components.ChatMessageInputField
import com.example.gigachataiassistant.ui.theme.AppAuthSecondaryButtonBackgroundDark
import com.example.gigachataiassistant.ui.theme.AppAuthSecondaryButtonBackgroundLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AppOnPrimary
import com.example.gigachataiassistant.ui.theme.AppPrimaryDark
import com.example.gigachataiassistant.ui.theme.AppPrimaryLight
import com.example.gigachataiassistant.ui.theme.AppTopBarTitleDark
import com.example.gigachataiassistant.ui.theme.AppTopBarTitleLight
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonsSpacing
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatAssistantShareGap
import com.example.gigachataiassistant.ui.theme.ChatBubbleMinWidth
import com.example.gigachataiassistant.ui.theme.CircularProgressStrokeWidth
import com.example.gigachataiassistant.ui.theme.CompactIconSize
import com.example.gigachataiassistant.ui.theme.ChatMessageBodyStyle
import com.example.gigachataiassistant.ui.theme.ChatMessageBubbleCornerRadius
import com.example.gigachataiassistant.ui.theme.ChatMessageBubbleExtraInset
import com.example.gigachataiassistant.ui.theme.ChatMessageBubbleSpacing
import com.example.gigachataiassistant.ui.theme.ChatInputRowBottomPadding
import com.example.gigachataiassistant.ui.theme.ChatSendButtonSize
import com.example.gigachataiassistant.ui.theme.ScreenTopBarTitleStyle
import com.example.gigachataiassistant.ui.theme.StandardIconSize
import com.example.gigachataiassistant.ui.theme.topAppBarContentColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current
    val shareChooserTitle = stringResource(R.string.chat_share)

    val title = uiState.title.ifEmpty { stringResource(R.string.chat_default_title) }
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val primaryColor = if (isDark) AppPrimaryDark else AppPrimaryLight

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val barColor = topAppBarContentColor()
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = ScreenTopBarTitleStyle,
                        color = barColor,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.chat_cd_back),
                            tint = barColor,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = barColor,
                    titleContentColor = barColor,
                    actionIconContentColor = barColor,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = AuthPrimaryButtonsSpacing),
                verticalArrangement = Arrangement.spacedBy(ChatMessageBubbleSpacing),
            ) {
                items(
                    items = uiState.messages,
                    key = { it.id },
                ) { message ->
                    MessageBubble(
                        message = message,
                        onShareAssistant = { text ->
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(send, shareChooserTitle))
                        },
                    )
                }
            }

            uiState.errorMessageId?.let { errId ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AuthScreenHorizontalPadding),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(errId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { viewModel.retry() }) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = AuthScreenHorizontalPadding)
                    .padding(end = AuthScreenHorizontalPadding)
                    .padding(bottom = ChatInputRowBottomPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ChatAssistantShareGap),
            ) {
                ChatMessageInputField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = stringResource(R.string.chat_message_hint),
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isSending,
                    imeAction = ImeAction.Send,
                    onImeAction = {
                        val trimmed = input.trim()
                        if (trimmed.isNotBlank() && !uiState.isSending) {
                            viewModel.sendMessage(input)
                            input = ""
                        }
                    },
                )
                if (uiState.isSending) {
                    Box(
                        modifier = Modifier.size(ChatSendButtonSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(StandardIconSize),
                            color = primaryColor,
                            strokeWidth = CircularProgressStrokeWidth,
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .size(ChatSendButtonSize)
                            .clickable(
                                enabled = input.trim().isNotBlank(),
                                onClick = {
                                    viewModel.sendMessage(input)
                                    input = ""
                                },
                            ),
                        shape = CircleShape,
                        color = primaryColor,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.send),
                                contentDescription = stringResource(R.string.chat_send),
                                tint = AppOnPrimary,
                                modifier = Modifier.size(CompactIconSize),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: MessageEntity,
    onShareAssistant: (String) -> Unit,
) {
    val isUser = message.role == MessageRole.USER
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark

    val userBubbleColor = if (isDark) AppPrimaryDark else AppPrimaryLight
    val assistantBubbleColor = if (isDark) {
        AppAuthSecondaryButtonBackgroundDark
    } else {
        AppAuthSecondaryButtonBackgroundLight
    }

    val userTextColor = AppOnPrimary
    val assistantTextColor = if (isDark) AppTopBarTitleDark else AppTopBarTitleLight

    val bubbleColor = if (isUser) userBubbleColor else assistantBubbleColor
    val textColor = if (isUser) userTextColor else assistantTextColor

    val lateralInset = AuthScreenHorizontalPadding + ChatMessageBubbleExtraInset

    if (isUser) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = lateralInset, end = AuthScreenHorizontalPadding),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Surface(
                shape = RoundedCornerShape(ChatMessageBubbleCornerRadius),
                color = bubbleColor,
            ) {
                MessageBubbleContent(
                    message = message,
                    textColor = textColor,
                )
            }
        }
    } else {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AuthScreenHorizontalPadding, end = lateralInset),
        ) {
            val maxBubbleWidth = (maxWidth - ChatSendButtonSize - ChatAssistantShareGap)
                .coerceAtLeast(ChatBubbleMinWidth)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
            ) {
                Surface(
                    modifier = Modifier.widthIn(max = maxBubbleWidth),
                    shape = RoundedCornerShape(ChatMessageBubbleCornerRadius),
                    color = bubbleColor,
                ) {
                    MessageBubbleContent(
                        message = message,
                        textColor = textColor,
                    )
                }
                Spacer(modifier = Modifier.width(ChatAssistantShareGap))
                Surface(
                    modifier = Modifier
                        .size(ChatSendButtonSize)
                        .clickable { onShareAssistant(message.content) },
                    shape = CircleShape,
                    color = assistantBubbleColor,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = stringResource(R.string.chat_share),
                            tint = assistantTextColor,
                            modifier = Modifier.size(CompactIconSize),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubbleContent(
    message: MessageEntity,
    textColor: Color,
) {
    Text(
        text = message.content,
        style = ChatMessageBodyStyle,
        color = textColor,
        modifier = Modifier.padding(AuthScreenHorizontalPadding),
    )
}
