package com.example.gigachataiassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gigachataiassistant.BuildConfig
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.auth.AuthRepositoryImpl
import com.example.gigachataiassistant.ui.auth.AuthErrorMapper
import com.example.gigachataiassistant.ui.auth.AuthViewModel
import com.example.gigachataiassistant.ui.auth.AuthViewModelFactory
import com.example.gigachataiassistant.ui.auth.rememberGoogleSignInLauncher

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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.auth_signup_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.chat_cd_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.auth_email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !uiState.isLoading,
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                label = { Text(stringResource(R.string.auth_password_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !uiState.isLoading,
            )
            Button(
                onClick = { viewModel.signUp(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !uiState.isLoading,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.auth_signup_submit))
                }
            }
            if (BuildConfig.FIREBASE_WEB_CLIENT_ID.isNotBlank()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                OutlinedButton(
                    onClick = launchGoogleSignIn,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                ) {
                    Text(stringResource(R.string.auth_sign_up_google))
                }
            }
            TextButton(onClick = onBackToLogin, enabled = !uiState.isLoading) {
                Text(stringResource(R.string.auth_signup_have_account))
            }
        }
    }
}
