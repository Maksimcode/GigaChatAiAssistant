package com.example.gigachataiassistant.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gigachataiassistant.BuildConfig
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.auth.AuthRepositoryImpl
import com.example.gigachataiassistant.ui.auth.AuthErrorMapper
import com.example.gigachataiassistant.ui.auth.AuthViewModel
import com.example.gigachataiassistant.ui.auth.AuthViewModelFactory
import com.example.gigachataiassistant.ui.auth.rememberGoogleSignInLauncher
import com.example.gigachataiassistant.ui.components.AuthGroupedCredentialFields
import com.example.gigachataiassistant.ui.components.AuthPrimaryButton
import com.example.gigachataiassistant.ui.components.AuthRegisterButton
import com.example.gigachataiassistant.ui.theme.AuthFieldsToPrimaryButtonSpacing
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonHeight
import com.example.gigachataiassistant.ui.theme.AuthPrimaryButtonsSpacing
import com.example.gigachataiassistant.ui.theme.AuthScreenHorizontalPadding
import com.example.gigachataiassistant.ui.theme.ScreenTopBarTitleStyle
import com.example.gigachataiassistant.ui.theme.topAppBarContentColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onBackToLogin: () -> Unit,
    onNavigateToChats: () -> Unit,
) {
    val appContext = LocalContext.current.applicationContext
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            AuthRepositoryImpl(appContext),
            AuthErrorMapper(),
        ),
    )

    val launchGoogleSignIn = rememberGoogleSignInLauncher(
        onSuccess = { viewModel.signInWithGoogle(it) },
        onDismissed = { },
        onError = { viewModel.onGoogleSignInLauncherError() },
    )

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val errorId = uiState.errorMessageId
    val messageText = if (errorId != null) stringResource(errorId) else null
    val isNetworkError = errorId == R.string.auth_error_network
    val retryLabel = stringResource(R.string.action_retry)

    LaunchedEffect(errorId, messageText, isNetworkError) {
        val msg = messageText ?: return@LaunchedEffect
        val result = if (isNetworkError) {
            snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = retryLabel,
            )
        } else {
            snackbarHostState.showSnackbar(msg)
        }
        when (result) {
            SnackbarResult.ActionPerformed -> viewModel.signUp(email, password)
            else -> viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.navigateToChats) {
        if (uiState.navigateToChats) {
            onNavigateToChats()
            viewModel.consumeNavigateToChats()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            val barColor = topAppBarContentColor()
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.auth_signup_title),
                        style = ScreenTopBarTitleStyle,
                        color = barColor,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.chat_cd_back),
                            tint = barColor,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = barColor,
                    titleContentColor = barColor,
                    actionIconContentColor = barColor,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        SignupScreenBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AuthScreenHorizontalPadding),
            email = email,
            password = password,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            isLoading = uiState.isLoading,
            onSignUpClick = { viewModel.signUp(email, password) },
            onAlreadyHaveAccountClick = onBackToLogin,
            showGoogleSignIn = BuildConfig.FIREBASE_WEB_CLIENT_ID.isNotBlank(),
            onGoogleSignInClick = launchGoogleSignIn,
        )
    }
}

@Composable
private fun SignupScreenBody(
    modifier: Modifier,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSignUpClick: () -> Unit,
    onAlreadyHaveAccountClick: () -> Unit,
    showGoogleSignIn: Boolean,
    onGoogleSignInClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AuthPrimaryButtonsSpacing),
            contentAlignment = Alignment.Center,
        ) {
            AuthGroupedCredentialFields(
                email = email,
                password = password,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                enabled = !isLoading,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AuthScreenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_signup_submit),
                onClick = onSignUpClick,
                enabled = !isLoading,
                loading = isLoading,
            )
            AuthRegisterButton(
                text = stringResource(R.string.auth_signup_have_account),
                onClick = onAlreadyHaveAccountClick,
                modifier = Modifier.padding(top = AuthPrimaryButtonsSpacing),
                enabled = !isLoading,
            )
            if (showGoogleSignIn) {
                SignupGoogleSignInSection(
                    modifier = Modifier.padding(top = AuthFieldsToPrimaryButtonSpacing),
                    enabled = !isLoading,
                    onGoogleSignInClick = onGoogleSignInClick,
                )
            }
        }
    }
}

@Composable
private fun SignupGoogleSignInSection(
    modifier: Modifier,
    enabled: Boolean,
    onGoogleSignInClick: () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(AuthFieldsToPrimaryButtonSpacing))
        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(AuthPrimaryButtonHeight),
            enabled = enabled,
        ) {
            Text(stringResource(R.string.auth_sign_up_google))
        }
    }
}
