package com.example.gigachataiassistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatListCardDateDark
import com.example.gigachataiassistant.ui.theme.ChatListCardDateLight
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleDark
import com.example.gigachataiassistant.ui.theme.ChatListCardTitleLight
import com.example.gigachataiassistant.ui.theme.ChatListSearchEmptyIllustrationTintDark
import com.example.gigachataiassistant.ui.theme.ChatListSearchEmptyIllustrationTintLight
import com.example.gigachataiassistant.ui.theme.ChatListSearchEmptySubtitleStyle
import com.example.gigachataiassistant.ui.theme.ChatListSearchEmptyTitleStyle
import com.example.gigachataiassistant.ui.theme.ChatListCardSpacing
import com.example.gigachataiassistant.ui.theme.ChatListSearchEmptyIllustrationSize

@Composable
fun ChatListSearchEmptyState(
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.background == AppBackgroundDark
    val illustrationTint = if (isDark) {
        ChatListSearchEmptyIllustrationTintDark
    } else {
        ChatListSearchEmptyIllustrationTintLight
    }
    val titleColor = if (isDark) ChatListCardTitleDark else ChatListCardTitleLight
    val subtitleColor = if (isDark) ChatListCardDateDark else ChatListCardDateLight

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = AuthScreenHorizontalPadding),
        ) {
            Icon(
                painter = painterResource(R.drawable.times_square),
                contentDescription = stringResource(R.string.chats_cd_search_empty),
                modifier = Modifier.size(ChatListSearchEmptyIllustrationSize),
                tint = illustrationTint,
            )
            Spacer(modifier = Modifier.height(ChatListCardSpacing))
            Text(
                text = stringResource(R.string.chats_search_empty_title),
                style = ChatListSearchEmptyTitleStyle,
                color = titleColor,
            )
            Spacer(modifier = Modifier.height(ChatListCardSpacing))
            Text(
                text = stringResource(R.string.chats_search_empty_subtitle),
                style = ChatListSearchEmptySubtitleStyle,
                color = subtitleColor,
            )
        }
    }
}
