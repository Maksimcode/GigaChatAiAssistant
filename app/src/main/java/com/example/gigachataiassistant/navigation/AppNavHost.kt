package com.example.gigachataiassistant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.gigachataiassistant.data.auth.AuthRepositoryImpl
import com.example.gigachataiassistant.ui.screens.ChatListScreen
import com.example.gigachataiassistant.ui.screens.ChatScreen
import com.example.gigachataiassistant.ui.screens.ImagesScreen
import com.example.gigachataiassistant.ui.screens.LoginScreen
import com.example.gigachataiassistant.ui.screens.ProfileScreen
import com.example.gigachataiassistant.ui.screens.SignupScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val authRepository = remember { AuthRepositoryImpl() }
    val startDestination =
        if (FirebaseAuth.getInstance().currentUser != null) ChatsDestination else LoginDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onNavigateToChats = {
                    navController.navigate(ChatsDestination) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate(SignupDestination) },
            )
        }
        composable<SignupDestination> {
            SignupScreen(
                onBackToLogin = { navController.popBackStack() },
                onNavigateToChats = {
                    navController.navigate(ChatsDestination) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                },
            )
        }
        composable<ChatsDestination> {
            ChatListScreen(
                onOpenChat = { chatId ->
                    navController.navigate(ChatDestination(chatId = chatId))
                },
                onOpenProfile = { navController.navigate(ProfileDestination) },
                onOpenImages = { navController.navigate(ImagesDestination) },
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
                onLogout = {
                    authRepository.signOut()
                    navController.navigate(LoginDestination) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }
        composable<ImagesDestination> {
            ImagesScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
