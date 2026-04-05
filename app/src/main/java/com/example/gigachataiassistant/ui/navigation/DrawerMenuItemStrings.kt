package com.example.gigachataiassistant.ui.navigation

import androidx.annotation.StringRes
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.navigation.DrawerMenuItem

@get:StringRes
val DrawerMenuItem.titleRes: Int
    get() = when (this) {
        DrawerMenuItem.Search -> R.string.nav_drawer_search
        DrawerMenuItem.NewChat -> R.string.nav_drawer_new_chat
        DrawerMenuItem.Images -> R.string.nav_drawer_images
        DrawerMenuItem.ChatList -> R.string.nav_drawer_home
        DrawerMenuItem.Profile -> R.string.nav_drawer_profile
    }
