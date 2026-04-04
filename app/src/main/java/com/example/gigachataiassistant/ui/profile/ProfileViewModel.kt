package com.example.gigachataiassistant.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigachataiassistant.data.local.AppDatabase
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.data.settings.SettingsRepository
import com.example.gigachataiassistant.domain.auth.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val currentTheme: AppTheme = AppTheme.SYSTEM,
    val isLoading: Boolean = false,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val database: AppDatabase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(user = authRepository.getCurrentUser()) }
        viewModelScope.launch {
            settingsRepository.themeFlow.collect { theme ->
                _uiState.update { it.copy(currentTheme = theme) }
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                database.clearAllTables()
                authRepository.signOut()
                onSuccess()
            } catch (_: Exception) {
                authRepository.signOut()
                onSuccess()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
