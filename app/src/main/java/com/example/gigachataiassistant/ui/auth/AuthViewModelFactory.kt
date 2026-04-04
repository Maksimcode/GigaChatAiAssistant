package com.example.gigachataiassistant.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gigachataiassistant.domain.auth.AuthRepository

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val errorMapper: AuthErrorMapper,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, errorMapper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}