package com.example.gigachataiassistant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.gigachataiassistant.data.auth.AuthRepositoryImpl
import com.example.gigachataiassistant.data.chat.ChatRepositoryImpl
import com.example.gigachataiassistant.data.chat.MessageRepositoryImpl
import com.example.gigachataiassistant.data.gigachat.GigaChatNetworkModule
import com.example.gigachataiassistant.data.local.AppDatabase
import com.example.gigachataiassistant.ui.chat.ChatViewModel
import com.example.gigachataiassistant.ui.chat.ChatViewModelFactory
import com.example.gigachataiassistant.ui.chats.ChatListViewModel
import com.example.gigachataiassistant.ui.chats.ChatListViewModelFactory
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
            val context = LocalContext.current
            val chatRepository = remember {
                ChatRepositoryImpl(AppDatabase.getInstance(context.applicationContext).chatDao())
            }
            val chatListViewModel: ChatListViewModel = viewModel(
                factory = ChatListViewModelFactory(chatRepository),
            )
            ChatListScreen(
                viewModel = chatListViewModel,
                onOpenChat = { chatId ->
                    navController.navigate(ChatDestination(chatId = chatId))
                },
                onOpenProfile = { navController.navigate(ProfileDestination) },
                onOpenImages = { navController.navigate(ImagesDestination) },
            )
        }
        composable<ChatDestination> { entry ->
            val route = entry.toRoute<ChatDestination>()
            val context = LocalContext.current
            val db = remember {
                AppDatabase.getInstance(context.applicationContext)
            }
            val messageRepository = remember {
                MessageRepositoryImpl(db.messageDao())
            }
            val chatRepository = remember {
                ChatRepositoryImpl(db.chatDao())
            }
            val gigaChat = remember {
                GigaChatNetworkModule.createRemoteDataSource()
            }
            val chatViewModel: ChatViewModel = viewModel(
                key = route.chatId,
                factory = ChatViewModelFactory(
                    chatId = route.chatId,
                    messageRepository = messageRepository,
                    chatRepository = chatRepository,
                    gigaChat = gigaChat,
                ),
            )
            ChatScreen(
                viewModel = chatViewModel,
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