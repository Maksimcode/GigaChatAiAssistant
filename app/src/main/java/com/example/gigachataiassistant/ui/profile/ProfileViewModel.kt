package com.example.gigachataiassistant.ui.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.data.gigachat.GigaChatBalanceEntry
import com.example.gigachataiassistant.data.gigachat.GigaChatBalanceOutcome
import com.example.gigachataiassistant.data.gigachat.GigaChatRemoteDataSource
import com.example.gigachataiassistant.data.local.AppDatabase
import com.example.gigachataiassistant.data.settings.AppTheme
import com.example.gigachataiassistant.data.settings.SettingsRepository
import android.net.Uri
import com.example.gigachataiassistant.domain.auth.AuthRepository
import com.example.gigachataiassistant.domain.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ProfileGigaChatQuotaState {
    data object Loading : ProfileGigaChatQuotaState()
    data class Loaded(val entries: List<GigaChatBalanceEntry>) : ProfileGigaChatQuotaState()
    data object NotAvailable : ProfileGigaChatQuotaState()
    data class Error(@param:StringRes val messageId: Int) : ProfileGigaChatQuotaState()
}

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val currentTheme: AppTheme = AppTheme.SYSTEM,
    val isLoading: Boolean = false,
    val isPhotoUploading: Boolean = false,
    val isEditing: Boolean = false,
    @param:StringRes val errorMessageId: Int? = null,
    val gigaChatQuota: ProfileGigaChatQuotaState = ProfileGigaChatQuotaState.Loading,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val database: AppDatabase,
    private val gigaChat: GigaChatRemoteDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refreshUser()
        refreshGigaChatQuota()
        viewModelScope.launch {
            settingsRepository.themeFlow.collect { theme ->
                _uiState.update { it.copy(currentTheme = theme) }
            }
        }
    }

    private fun refreshUser() {
        _uiState.update { it.copy(user = authRepository.getCurrentUser()) }
    }

    fun refreshGigaChatQuota() {
        viewModelScope.launch {
            _uiState.update { it.copy(gigaChatQuota = ProfileGigaChatQuotaState.Loading) }
            when (val outcome = gigaChat.getBalance()) {
                is GigaChatBalanceOutcome.Success ->
                    _uiState.update {
                        it.copy(gigaChatQuota = ProfileGigaChatQuotaState.Loaded(outcome.entries))
                    }
                GigaChatBalanceOutcome.NotAvailable ->
                    _uiState.update { it.copy(gigaChatQuota = ProfileGigaChatQuotaState.NotAvailable) }
                is GigaChatBalanceOutcome.Failure ->
                    _uiState.update {
                        it.copy(gigaChatQuota = ProfileGigaChatQuotaState.Error(R.string.profile_tokens_error))
                    }
            }
        }
    }

    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing, errorMessageId = null) }
    }

    fun uploadProfilePhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPhotoUploading = true, errorMessageId = null) }
            try {
                when (authRepository.updateProfilePhoto(uri)) {
                    is AuthResult.Success -> refreshUser()
                    is AuthResult.Failure ->
                        _uiState.update { it.copy(errorMessageId = R.string.profile_upload_error) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessageId = R.string.profile_error_generic) }
            } finally {
                _uiState.update { it.copy(isPhotoUploading = false) }
            }
        }
    }

    fun updateProfile(newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageId = null) }
            try {
                val result = authRepository.updateProfile(newName)
                if (result is AuthResult.Success) {
                    refreshUser()
                    _uiState.update { it.copy(isEditing = false) }
                } else if (result is AuthResult.Failure) {
                    _uiState.update { it.copy(errorMessageId = R.string.profile_error_update) }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(errorMessageId = R.string.profile_error_generic)
                }
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
