package com.example.gigachataiassistant.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

inline fun <reified T : Any> NavController.navigateToTopLevel(
    route: T,
    noinline options: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route) {
        popUpTo<ChatsDestination> {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
        options()
    }
}

fun NavController.navigateDrawerMenuItem(item: DrawerMenuItem) {
    when (item) {
        DrawerMenuItem.Search,
        DrawerMenuItem.NewChat,
        DrawerMenuItem.ChatList,
        -> navigateToTopLevel(ChatsDestination)
        DrawerMenuItem.Images -> navigateToTopLevel(ImagesDestination)
        DrawerMenuItem.Profile -> navigateToTopLevel(ProfileDestination)
    }
}
