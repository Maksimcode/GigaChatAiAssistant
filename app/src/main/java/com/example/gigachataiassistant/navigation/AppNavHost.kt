package com.example.gigachataiassistant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.gigachataiassistant.data.auth.AuthRepositoryImpl
import com.example.gigachataiassistant.data.chat.ChatRepository
import com.example.gigachataiassistant.data.chat.ChatRepositoryImpl
import com.example.gigachataiassistant.data.chat.MessageRepositoryImpl
import com.example.gigachataiassistant.data.gigachat.GigaChatNetworkModule
import com.example.gigachataiassistant.data.local.AppDatabase
import com.example.gigachataiassistant.data.settings.SettingsRepository
import com.example.gigachataiassistant.ui.chat.ChatViewModel
import com.example.gigachataiassistant.ui.chat.ChatViewModelFactory
import com.example.gigachataiassistant.ui.chats.ChatListViewModel
import com.example.gigachataiassistant.ui.chats.ChatListViewModelFactory
import com.example.gigachataiassistant.ui.profile.ProfileViewModel
import com.example.gigachataiassistant.ui.profile.ProfileViewModelFactory
import com.example.gigachataiassistant.ui.screens.ChatListScreen
import com.example.gigachataiassistant.ui.screens.ChatScreen
import com.example.gigachataiassistant.ui.screens.ImagesScreen
import com.example.gigachataiassistant.ui.screens.LoginScreen
import com.example.gigachataiassistant.ui.screens.ProfileScreen
import com.example.gigachataiassistant.ui.screens.SignupScreen
import com.example.gigachataiassistant.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepositoryImpl() }
    val settingsRepository = remember { SettingsRepository(context.applicationContext) }
    val firebaseAuth = FirebaseAuth.getInstance()
    val startDestination =
        if (firebaseAuth.currentUser != null) ChatsDestination else LoginDestination

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
                onBackToLogin = { navController.popBackStackIfPossible() },
                onNavigateToChats = {
                    navController.navigate(ChatsDestination) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                },
            )
        }
        composable<ChatsDestination> {
            val context = LocalContext.current
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""
            val chatRepository = remember {
                ChatRepositoryImpl(AppDatabase.getInstance(context.applicationContext).chatDao())
            }
            val chatListViewModel: ChatListViewModel = viewModel(
                factory = ChatListViewModelFactory(currentUserId, chatRepository),
            )
            ChatListScreen(
                viewModel = chatListViewModel,
                onOpenChat = { chatId ->
                    navController.navigate(ChatDestination(chatId = chatId))
                },
                selectedDrawerItem = DrawerMenuItem.ChatList,
                onDrawerNavigate = { navController.navigateDrawerItem(it) },
            )
        }
        composable<ChatDestination> { entry ->
            val route = entry.toRoute<ChatDestination>()
            val context = LocalContext.current
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""
            val defaultNewChatTitle = stringResource(R.string.chats_new_chat)
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
                    userId = currentUserId,
                    messageRepository = messageRepository,
                    chatRepository = chatRepository,
                    gigaChat = gigaChat,
                    defaultNewChatTitle = defaultNewChatTitle,
                ),
            )
            ChatScreen(
                viewModel = chatViewModel,
                onBack = { navController.popBackStackIfPossible() },
            )
        }
        composable<ProfileDestination> {
            val scope = rememberCoroutineScope()
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""
            val chatRepository = remember {
                ChatRepositoryImpl(AppDatabase.getInstance(context.applicationContext).chatDao())
            }
            val newChatTitle = stringResource(R.string.chats_new_chat)
            val db = remember { AppDatabase.getInstance(context.applicationContext) }
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(authRepository, settingsRepository, db)
            )
            ProfileScreen(
                viewModel = profileViewModel,
                selectedDrawerItem = DrawerMenuItem.Profile,
                onDrawerNavigate = { item ->
                    handleDrawerNavigation(
                        navController = navController,
                        scope = scope,
                        chatRepository = chatRepository,
                        userId = currentUserId,
                        newChatTitle = newChatTitle,
                        item = item,
                    )
                },
                onBack = { navController.popBackStackIfPossible() },
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }
        composable<ImagesDestination> {
            val scope = rememberCoroutineScope()
            val currentUserId = firebaseAuth.currentUser?.uid ?: ""
            val chatRepository = remember {
                ChatRepositoryImpl(AppDatabase.getInstance(context.applicationContext).chatDao())
            }
            val newChatTitle = stringResource(R.string.chats_new_chat)
            ImagesScreen(
                selectedDrawerItem = DrawerMenuItem.Images,
                onDrawerNavigate = { item ->
                    handleDrawerNavigation(
                        navController = navController,
                        scope = scope,
                        chatRepository = chatRepository,
                        userId = currentUserId,
                        newChatTitle = newChatTitle,
                        item = item,
                    )
                },
                onBack = { navController.popBackStackIfPossible() },
            )
        }
    }
}

private fun handleDrawerNavigation(
    navController: NavController,
    scope: CoroutineScope,
    chatRepository: ChatRepository,
    userId: String,
    newChatTitle: String,
    item: DrawerMenuItem,
) {
    when (item) {
        DrawerMenuItem.NewChat -> scope.launch {
            val id = chatRepository.createChat(userId, newChatTitle)
            navController.navigate(ChatDestination(chatId = id)) {
                popUpTo<ChatsDestination> { inclusive = false }
            }
        }
        else -> navController.navigateDrawerItem(item)
    }
}
