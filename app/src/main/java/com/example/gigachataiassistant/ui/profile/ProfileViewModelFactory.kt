package com.example.gigachataiassistant.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gigachataiassistant.data.local.AppDatabase
import com.example.gigachataiassistant.data.settings.SettingsRepository
import com.example.gigachataiassistant.domain.auth.AuthRepository

class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(authRepository, settingsRepository, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
