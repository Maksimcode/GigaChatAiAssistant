package com.example.gigachataiassistant.navigation

sealed class DrawerMenuItem {

    data object Search : DrawerMenuItem()

    data object NewChat : DrawerMenuItem()

    data object Images : DrawerMenuItem()

    data object ChatList : DrawerMenuItem()

    data object Profile : DrawerMenuItem()

    companion object {
        val ordered: List<DrawerMenuItem> = listOf(
            Search,
            NewChat,
            Images,
            ChatList,
            Profile,
        )
    }
}
