package com.example.gigachataiassistant.navigation

import androidx.navigation.NavController

fun NavController.popBackStackIfPossible(): Boolean {
    if (previousBackStackEntry == null) return false
    return popBackStack()
}
