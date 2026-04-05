package com.example.gigachataiassistant.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute

fun NavController.goToChatsList() {
    if (currentDestination?.hasRoute<ChatsDestination>() == true) return
    navigate(ChatsDestination) {
        popUpTo<ChatsDestination> {
            inclusive = false
        }
        launchSingleTop = true
    }
}

private fun NavController.navigateToProfileFromDrawer() {
    if (currentDestination?.hasRoute<ProfileDestination>() == true) return
    navigate(ProfileDestination) {
        popUpTo<ChatsDestination> { inclusive = false }
        launchSingleTop = true
    }
}

private fun NavController.navigateToImagesFromDrawer() {
    if (currentDestination?.hasRoute<ImagesDestination>() == true) return
    navigate(ImagesDestination) {
        popUpTo<ChatsDestination> { inclusive = false }
        launchSingleTop = true
    }
}

fun NavController.navigateDrawerItem(item: DrawerMenuItem) {
    when (item) {
        DrawerMenuItem.ChatList -> goToChatsList()
        DrawerMenuItem.NewChat -> Unit
        DrawerMenuItem.Images -> navigateToImagesFromDrawer()
        DrawerMenuItem.Profile -> navigateToProfileFromDrawer()
    }
}
