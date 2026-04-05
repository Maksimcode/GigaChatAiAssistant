package com.example.gigachataiassistant.navigation

sealed class DrawerMenuItem {

    data object NewChat : DrawerMenuItem()

    data object Images : DrawerMenuItem()

    data object ChatList : DrawerMenuItem()

    data object Profile : DrawerMenuItem()
}
