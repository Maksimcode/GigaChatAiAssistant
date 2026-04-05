package com.example.gigachataiassistant.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthInputStrokeWidth
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonHeight
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonTextStyle
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonsSpacing
import com.example.gigachataiassistant.ui.theme.AuthRegisterButtonCornerRadius
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatListCardDateDark
import com.example.gigachataiassistant.ui.theme.ChatListCardDateLight
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleDark
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleLight
import com.example.gigachataiassistant.ui.theme.ChatMessageBodyStyle
import com.example.gigachataiassistant.ui.theme.ClearHistoryOverlayZIndex
import com.example.gigachataiassistant.ui.theme.ClearHistoryScrimDark
import com.example.gigachataiassistant.ui.theme.ClearHistoryScrimLight
import com.example.gigachataiassistant.ui.theme.ClearHistorySheetAnimationMillis
import com.example.gigachataiassistant.ui.theme.ClearHistorySheetEdgePadding
import com.example.gigachataiassistant.ui.theme.ClearHistorySheetHeight
import com.example.gigachataiassistant.ui.theme.ClearHistorySheetTitleStyle
import com.example.gigachataiassistant.ui.theme.ClearHistorySheetTopCorner
import com.example.gigachataiassistant.ui.theme.NavDrawerSearchFieldBottomSpacing
import com.example.gigachataiassistant.ui.theme.SurfaceTonalElevationNone
import com.example.gigachataiassistant.ui.theme.ThemeAlpha

@Composable
fun ChatListClearHistoryOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val scrimColor = if (isDark) {
        ClearHistoryScrimDark.copy(alpha = ThemeAlpha.ClearHistoryScrimAlpha)
    } else {
        ClearHistoryScrimLight.copy(alpha = ThemeAlpha.ClearHistoryScrimAlpha)
    }
    val titleColor = if (isDark) ChatListCardTitleDark else ChatListCardTitleLight
    val bodyColor = if (isDark) ChatListCardDateDark else ChatListCardDateLight

    BackHandler(enabled = visible) {
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(ClearHistorySheetAnimationMillis.ScrimFadeIn),
        ),
        exit = fadeOut(
            animationSpec = tween(ClearHistorySheetAnimationMillis.ScrimFadeOut),
        ),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(ClearHistoryOverlayZIndex),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
            )
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(ClearHistorySheetAnimationMillis.SheetSlideIn),
                    initialOffsetY = { it },
                ) + fadeIn(
                    animationSpec = tween(ClearHistorySheetAnimationMillis.SheetContentFadeIn),
                ),
                exit = slideOutVertically(
                    animationSpec = tween(ClearHistorySheetAnimationMillis.SheetSlideOut),
                    targetOffsetY = { it },
                ) + fadeOut(
                    animationSpec = tween(ClearHistorySheetAnimationMillis.SheetContentFadeOut),
                ),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(ClearHistorySheetHeight),
                    shape = RoundedCornerShape(
                        topStart = ClearHistorySheetTopCorner,
                        topEnd = ClearHistorySheetTopCorner,
                    ),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = SurfaceTonalElevationNone,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = ClearHistorySheetEdgePadding),
                    ) {
                        Text(
                            text = stringResource(R.string.chats_clear_history_sheet_title),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ClearHistorySheetEdgePadding),
                            style = ClearHistorySheetTitleStyle,
                            color = titleColor,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(AuthPrimaryButtonsSpacing))
                        HorizontalDivider(
                            thickness = AuthInputStrokeWidth,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                        Spacer(modifier = Modifier.height(AuthPrimaryButtonsSpacing))
                        Text(
                            text = stringResource(R.string.chats_clear_history_sheet_message),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ClearHistorySheetEdgePadding),
                            style = ChatMessageBodyStyle,
                            color = bodyColor,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(AuthPrimaryButtonsSpacing))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ClearHistorySheetEdgePadding)
                                .padding(
                                    horizontal = AuthScreenHorizontalPadding,
                                    vertical = NavDrawerSearchFieldBottomSpacing,
                                ),
                            horizontalArrangement = Arrangement.spacedBy(AuthPrimaryButtonsSpacing),
                        ) {
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(AuthPrimaryButtonHeight),
                                shape = RoundedCornerShape(AuthRegisterButtonCornerRadius),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
                                    disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
                                ),
                            ) {
                                Text(
                                    text = stringResource(R.string.chats_clear_history_cancel),
                                    style = AuthPrimaryButtonTextStyle,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                )
                            }
                            Button(
                                onClick = onConfirm,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(AuthPrimaryButtonHeight),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = ThemeAlpha.DisabledComponentAlpha),
                                ),
                            ) {
                                Text(
                                    text = stringResource(R.string.chats_clear_history_delete),
                                    style = AuthPrimaryButtonTextStyle,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(AuthPrimaryButtonsSpacing))
                    }
                }
            }
        }
    }
}
