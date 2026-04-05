package com.example.gigachataiassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.navigation.DrawerMenuItem
import com.example.gigachataiassistant.ui.chats.ChatListViewModel
import com.example.gigachataiassistant.ui.components.ChatListCard
import com.example.gigachataiassistant.ui.components.ChatListClearHistoryOverlay
import com.example.gigachataiassistant.ui.components.ChatListSearchEmptyState
import com.example.gigachataiassistant.ui.components.ChatListSearchField
import com.example.gigachataiassistant.ui.navigation.MainModalNavigationDrawer
import com.example.gigachataiassistant.ui.theme.AppBackgroundDark
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonsSpacing
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ChatInputRowBottomPadding
import com.example.gigachataiassistant.ui.theme.ChatListRetryButtonTopSpacing
import com.example.gigachataiassistant.ui.theme.NavDrawerSearchFieldBottomSpacing
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconDark
import com.example.gigachataiassistant.ui.theme.ChatListAccentIconLight
import com.example.gigachataiassistant.ui.theme.ChatListCardSpacing
import com.example.gigachataiassistant.ui.theme.ScreenTopBarTitleStyle
import com.example.gigachataiassistant.ui.theme.topAppBarContentColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    onOpenChat: (String) -> Unit,
    selectedDrawerItem: DrawerMenuItem,
    onDrawerNavigate: (DrawerMenuItem) -> Unit,
) {
    val searchInput by viewModel.searchInput.collectAsState()
    val appliedSearchQuery by viewModel.appliedSearchQuery.collectAsState()
    val lazyPagingItems = viewModel.chats.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    MainModalNavigationDrawer(
        drawerState = drawerState,
        selectedItem = selectedDrawerItem,
        onDrawerItemClick = { item ->
            when (item) {
                DrawerMenuItem.NewChat -> scope.launch {
                    drawerState.close()
                    val id = viewModel.createNewChat()
                    onOpenChat(id)
                }
                else -> {
                    scope.launch { drawerState.close() }
                    onDrawerNavigate(item)
                }
            }
        },
        searchQuery = searchInput,
        onSearchQueryChange = viewModel::onSearchInputChange,
        onSearchSubmit = viewModel::applySearch,
        showSearchField = true,
    ) {
        var showClearHistorySheet by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                val barColor = topAppBarContentColor()
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.chats_title),
                            style = ScreenTopBarTitleStyle,
                            color = barColor,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.nav_drawer_open_menu),
                                tint = barColor,
                            )
                        }
                    },
                    actions = {
                        val trashTint = if (MaterialTheme.colorScheme.background == AppBackgroundDark) {
                            ChatListAccentIconDark
                        } else {
                            ChatListAccentIconLight
                        }
                        IconButton(onClick = { showClearHistorySheet = true }) {
                            Icon(
                                painter = painterResource(R.drawable.trash),
                                contentDescription = stringResource(R.string.chats_cd_clear_history),
                                tint = trashTint,
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val id = viewModel.createNewChat()
                            onOpenChat(id)
                        }
                    },
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.chats_new_chat),
                    )
                }
            },
        ) { padding ->
            val refreshState = lazyPagingItems.loadState.refresh
            val showSearchEmpty =
                refreshState is LoadState.NotLoading &&
                    lazyPagingItems.itemCount == 0 &&
                    appliedSearchQuery.isNotEmpty()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                if (showSearchEmpty) {
                    ChatListSearchEmptyState(modifier = Modifier.fillMaxSize())
                }
                Column(Modifier.fillMaxSize()) {
                    ChatListSearchField(
                        value = searchInput,
                        onValueChange = viewModel::onSearchInputChange,
                        onSearchClick = viewModel::applySearch,
                        placeholder = stringResource(R.string.chats_search_label),
                        searchIconContentDescription = stringResource(R.string.chats_search_action),
                        modifier = Modifier.padding(
                            horizontal = AuthScreenHorizontalPadding,
                            vertical = NavDrawerSearchFieldBottomSpacing,
                        ),
                    )
                    when {
                        showSearchEmpty -> {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        refreshState is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        refreshState is LoadState.Error && lazyPagingItems.itemCount == 0 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                                    .padding(ChatInputRowBottomPadding),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = stringResource(R.string.chats_load_error),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                )
                                Button(
                                    onClick = { lazyPagingItems.retry() },
                                    modifier = Modifier.padding(top = ChatListRetryButtonTopSpacing),
                                ) {
                                    Text(stringResource(R.string.action_retry))
                                }
                            }
                        }

                        refreshState is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(R.string.chats_empty),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                contentPadding = PaddingValues(top = ChatListCardSpacing),
                            ) {
                                items(
                                    count = lazyPagingItems.itemCount,
                                    key = lazyPagingItems.itemKey { it.id },
                                ) { index ->
                                    val chat = lazyPagingItems[index]
                                    if (chat != null) {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            if (index > 0) {
                                                Spacer(modifier = Modifier.height(ChatListCardSpacing))
                                            }
                                            ChatListCard(
                                                chat = chat,
                                                onClick = { onOpenChat(chat.id) },
                                                onDelete = { viewModel.deleteChat(chat.id) },
                                                modifier = Modifier.padding(horizontal = AuthScreenHorizontalPadding),
                                            )
                                        }
                                    }
                                }
                                if (lazyPagingItems.loadState.append is LoadState.Loading) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(AuthPrimaryButtonsSpacing),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                if (lazyPagingItems.loadState.append is LoadState.Error) {
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(AuthPrimaryButtonsSpacing),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text(
                                                text = stringResource(R.string.chats_append_error),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                            Button(
                                                onClick = { lazyPagingItems.retry() },
                                                modifier = Modifier.padding(top = NavDrawerSearchFieldBottomSpacing),
                                            ) {
                                                Text(stringResource(R.string.action_retry))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ChatListClearHistoryOverlay(
            visible = showClearHistorySheet,
            onDismiss = { showClearHistorySheet = false },
            onConfirm = {
                viewModel.clearAllChats()
                showClearHistorySheet = false
            },
        )
        }
    }
}
