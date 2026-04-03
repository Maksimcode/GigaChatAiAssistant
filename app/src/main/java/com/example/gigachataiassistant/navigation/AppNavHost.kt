package com.example.gigachataiassistant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.gigachataiassistant.ui.screens.ChatListScreen
import com.example.gigachataiassistant.ui.screens.ChatScreen
import com.example.gigachataiassistant.ui.screens.ImagesScreen
import com.example.gigachataiassistant.ui.screens.LoginScreen
import com.example.gigachataiassistant.ui.screens.ProfileScreen
import com.example.gigachataiassistant.ui.screens.SignupScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier,
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onOpenChatList = { navController.navigate(ChatsDestination) },
                onOpenSignup = { navController.navigate(SignupDestination) },
            )
        }
        composable<SignupDestination> {
            SignupScreen(
                onBackToLogin = { navController.popBackStack() },
                onOpenChatList = { navController.navigate(ChatsDestination) },
            )
        }

        composable<ChatsDestination> {
            ChatListScreen(
                onOpenChat = { chatId ->
                    navController.navigate(ChatDestination(chatId = chatId))
                },
                onOpenProfile = { navController.navigate(ProfileDestination) },
                onOpenImages = { navController.navigate(ImagesDestination) },
                onOpenLogin = { navController.navigate(LoginDestination) },
            )
        }
        composable<ChatDestination> { entry ->
            val route = entry.toRoute<ChatDestination>()
            ChatScreen(
                chatId = route.chatId,
                onBack = { navController.popBackStack() },
            )
        }
        composable<ProfileDestination> {
            ProfileScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable<ImagesDestination> {
            ImagesScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
