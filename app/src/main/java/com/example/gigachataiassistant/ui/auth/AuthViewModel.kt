package com.example.gigachataiassistant.ui.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.domain.auth.AuthError
import com.example.gigachataiassistant.domain.auth.AuthRepository
import com.example.gigachataiassistant.domain.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageId: Int? = null,
    val navigateToChats: Boolean = false,
)

class AuthViewModel(
    private val repository: AuthRepository,
    private val errorMapper: AuthErrorMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageId = errorMapper.toMessageRes(AuthError.EmptyFields),
                    )
                }
                return@launch
            }
            val trimmed = email.trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageId = errorMapper.toMessageRes(AuthError.InvalidEmail),
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessageId = null) }

            when (val result = repository.signInWithEmail(trimmed, password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, navigateToChats = true)
                    }
                }
                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageId = errorMapper.toMessageRes(result.error),
                        )
                    }
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageId = errorMapper.toMessageRes(AuthError.EmptyFields),
                    )
                }
                return@launch
            }
            val trimmed = email.trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageId = errorMapper.toMessageRes(AuthError.InvalidEmail),
                    )
                }
                return@launch
            }
            if (password.length < 6) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageId = errorMapper.toMessageRes(AuthError.WeakPassword),
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessageId = null) }

            when (val result = repository.signUpWithEmail(trimmed, password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, navigateToChats = true)
                    }
                }
                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageId = errorMapper.toMessageRes(result.error),
                        )
                    }
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageId = null) }
            when (val result = repository.signInWithGoogle(idToken)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, navigateToChats = true)
                    }
                }
                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageId = errorMapper.toMessageRes(result.error),
                        )
                    }
                }
            }
        }
    }

    fun onGoogleSignInLauncherError() {
        _uiState.update {
            it.copy(isLoading = false, errorMessageId = R.string.auth_error_google)
        }
    }

    fun consumeNavigateToChats() {
        _uiState.update { it.copy(navigateToChats = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessageId = null) }
    }
}