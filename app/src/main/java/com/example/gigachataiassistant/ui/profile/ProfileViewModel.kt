package com.example.gigachataiassistant.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigachataiassistant.data.local.AppDatabase
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.data.settings.SettingsRepository
import com.example.gigachataiassistant.domain.auth.AuthRepository
import com.example.gigachataiassistant.domain.auth.AuthResult
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
    val isEditing: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val database: AppDatabase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refreshUser()
        viewModelScope.launch {
            settingsRepository.themeFlow.collect { theme ->
                _uiState.update { it.copy(currentTheme = theme) }
            }
        }
    }

    private fun refreshUser() {
        _uiState.update { it.copy(user = authRepository.getCurrentUser()) }
    }

    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing, error = null) }
    }

    fun updateProfile(newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = authRepository.updateProfile(newName)
                if (result is AuthResult.Success) {
                    refreshUser()
                    _uiState.update { it.copy(isEditing = false) }
                } else if (result is AuthResult.Failure) {
                    _uiState.update { it.copy(error = "Ошибка обновления профиля") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
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
