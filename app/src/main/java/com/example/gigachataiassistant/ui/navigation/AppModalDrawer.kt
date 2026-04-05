package com.example.gigachataiassistant.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.navigation.DrawerMenuItem
import com.example.gigachataiassistant.ui.components.ChatListSearchField
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.NavDrawerHeaderHorizontalPadding
import com.example.gigachataiassistant.ui.theme.NavDrawerHeaderVerticalPadding
import com.example.gigachataiassistant.ui.theme.NavDrawerItemHorizontalPadding
import com.example.gigachataiassistant.ui.theme.NavDrawerSearchFieldBottomSpacing
import com.example.gigachataiassistant.ui.theme.NavDrawerTopSpacerHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainModalNavigationDrawer(
    drawerState: DrawerState,
    selectedItem: DrawerMenuItem,
    onDrawerItemClick: (DrawerMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSearchSubmit: () -> Unit = {},
    showSearchField: Boolean = false,
    content: @Composable () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(NavDrawerTopSpacerHeight))
                Text(
                    text = stringResource(R.string.nav_drawer_header),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        horizontal = NavDrawerHeaderHorizontalPadding,
                        vertical = NavDrawerHeaderVerticalPadding,
                    ),
                )
                if (showSearchField) {
                    ChatListSearchField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        onSearchClick = {
                            onSearchSubmit()
                            focusManager.clearFocus()
                        },
                        placeholder = stringResource(R.string.chats_search_label),
                        searchIconContentDescription = stringResource(R.string.chats_search_action),
                        modifier = Modifier
                            .padding(horizontal = AuthScreenHorizontalPadding)
                            .fillMaxWidth(),
                    )
                    Spacer(Modifier.height(NavDrawerSearchFieldBottomSpacing))
                }
                DrawerNavRow(
                    titleRes = R.string.nav_drawer_new_chat,
                    icon = Icons.Default.Add,
                    selected = selectedItem == DrawerMenuItem.NewChat,
                    onClick = { onDrawerItemClick(DrawerMenuItem.NewChat) },
                )
                DrawerNavRow(
                    titleRes = R.string.nav_drawer_images,
                    icon = Icons.Default.Image,
                    selected = selectedItem == DrawerMenuItem.Images,
                    onClick = { onDrawerItemClick(DrawerMenuItem.Images) },
                )
                DrawerNavRow(
                    titleRes = R.string.nav_drawer_home,
                    icon = Icons.Default.Home,
                    selected = selectedItem == DrawerMenuItem.ChatList,
                    onClick = { onDrawerItemClick(DrawerMenuItem.ChatList) },
                )
                DrawerNavRow(
                    titleRes = R.string.nav_drawer_profile,
                    icon = Icons.Default.Person,
                    selected = selectedItem == DrawerMenuItem.Profile,
                    onClick = { onDrawerItemClick(DrawerMenuItem.Profile) },
                )
            }
        },
        content = content,
    )
}

@Composable
private fun DrawerNavRow(
    titleRes: Int,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
        label = { Text(stringResource(titleRes)) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = NavDrawerItemHorizontalPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    )
}
