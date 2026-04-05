package com.example.gigachataiassistant.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.local.ChatEntity
import com.example.gigachataiassistant.ui.chats.ChatListDateFormatter
import com.example.gigachataiassistant.ui.theme.AppAuthSecondaryButtonBackgroundDark
import com.example.gigachataiassistant.ui.theme.AppAuthSecondaryButtonBackgroundLight
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconDark
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconLight
import com.example.gigachataiassistant.ui.theme.ChatListCardCornerRadius
import com.example.gigachataiassistant.ui.theme.ChatListCardDateDark
import com.example.gigachataiassistant.ui.theme.ChatListCardDateLight
import com.example.gigachataiassistant.ui.theme.ChatListCardDateStyle
import com.example.gigachataiassistant.ui.theme.ChatListCardHeight
import com.example.gigachataiassistant.ui.theme.ChatListCardSingleLineVerticalPadding
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleDark
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleLight
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleDateSpacing
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleStyle
import com.example.gigachataiassistant.ui.theme.ChatListCardTwoLineVerticalPadding
import com.example.gigachataiassistant.ui.theme.ChatListCardSpacing
import com.example.gigachataiassistant.ui.theme.ChatListCardSwipeAnimationMillis
import com.example.gigachataiassistant.ui.theme.ChatListSwipeDeleteBackground
import com.example.gigachataiassistant.ui.theme.ChatListSwipeRevealWidth
import com.example.gigachataiassistant.ui.theme.CornerRadiusNone
import com.example.gigachataiassistant.ui.theme.StandardIconSize
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun ChatListCard(
    chat: ChatEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val cardColor = if (isDark) {
        AppAuthSecondaryButtonBackgroundDark
    } else {
        AppAuthSecondaryButtonBackgroundLight
    }
    val titleColor = if (isDark) ChatListCardTitleDark else ChatListCardTitleLight
    val dateColor = if (isDark) ChatListCardDateDark else ChatListCardDateLight

    var titleLineCount by remember(chat.id, chat.title) { mutableIntStateOf(1) }
    val verticalContentPadding = if (titleLineCount <= 1) {
        ChatListCardSingleLineVerticalPadding
    } else {
        ChatListCardTwoLineVerticalPadding
    }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val maxPx = with(density) { ChatListSwipeRevealWidth.toPx() }
    val offsetX = remember(chat.id) { Animatable(0f) }

    val outerClipShape = RoundedCornerShape(ChatListCardCornerRadius)
    val deleteStripShape = RoundedCornerShape(
        topStart = CornerRadiusNone,
        topEnd = ChatListCardCornerRadius,
        bottomEnd = ChatListCardCornerRadius,
        bottomStart = CornerRadiusNone,
    )
    val swipeProgress = (-offsetX.value / maxPx).coerceIn(0f, 1f)
    val r = ChatListCardCornerRadius
    val rightCornerRadius = r * (1f - swipeProgress)
    val surfaceShape = RoundedCornerShape(
        topStart = r,
        topEnd = rightCornerRadius,
        bottomEnd = rightCornerRadius,
        bottomStart = r,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ChatListCardHeight)
            .clip(outerClipShape),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(ChatListSwipeRevealWidth)
                    .fillMaxHeight()
                    .background(ChatListSwipeDeleteBackground, deleteStripShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDelete,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = stringResource(R.string.chats_cd_swipe_delete),
                    modifier = Modifier.size(StandardIconSize),
                    tint = Color.White,
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(ChatListCardHeight)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(chat.id, maxPx) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(
                                    (offsetX.value + dragAmount).coerceIn(-maxPx, 0f),
                                )
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                val target = if (offsetX.value < -maxPx / 2f) -maxPx else 0f
                                offsetX.animateTo(
                                    target,
                                    tween(durationMillis = ChatListCardSwipeAnimationMillis.SnapDuration),
                                )
                                if (target == -maxPx) {
                                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                }
                            }
                        },
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (offsetX.value < -1f) {
                            scope.launch {
                                offsetX.animateTo(
                                    0f,
                                    tween(durationMillis = ChatListCardSwipeAnimationMillis.SnapDuration),
                                )
                            }
                        } else {
                            onClick()
                        }
                    },
                ),
            shape = surfaceShape,
            color = cardColor,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AuthScreenHorizontalPadding)
                        .padding(vertical = verticalContentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ChatListCardSpacing),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.chat),
                        contentDescription = stringResource(R.string.chats_cd_chat_icon),
                        modifier = Modifier.size(StandardIconSize),
                        tint = if (isDark) ChatListAccentIconDark else ChatListAccentIconLight,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = chat.title,
                            style = ChatListCardTitleStyle,
                            color = titleColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { layoutResult ->
                                val lines = layoutResult.lineCount.coerceIn(1, 2)
                                if (lines != titleLineCount) {
                                    titleLineCount = lines
                                }
                            },
                        )
                        Spacer(modifier = Modifier.height(ChatListCardTitleDateSpacing))
                        Text(
                            text = ChatListDateFormatter.format(chat.lastActivityAt),
                            style = ChatListCardDateStyle,
                            color = dateColor,
                        )
                    }
                }
            }
        }
    }
}
