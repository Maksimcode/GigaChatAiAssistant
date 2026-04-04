package com.example.gigachataiassistant.ui.auth

import androidx.annotation.StringRes
import com.example.gigachataiassistant.R
import com.example.gigachataiassistant.domain.auth.AuthError

class AuthErrorMapper {
    @StringRes
    fun toMessageRes(error: AuthError): Int = when (error) {
        AuthError.EmptyFields -> R.string.auth_error_empty_fields
        AuthError.InvalidEmail -> R.string.auth_error_invalid_email
        AuthError.WrongPassword -> R.string.auth_error_wrong_password
        AuthError.UserNotFound -> R.string.auth_error_user_not_found
        AuthError.EmailAlreadyInUse -> R.string.auth_error_email_in_use
        AuthError.WeakPassword -> R.string.auth_error_weak_password
        AuthError.Network -> R.string.auth_error_network
        AuthError.InvalidCredential -> R.string.auth_error_invalid_credentials
        AuthError.Unknown -> R.string.auth_error_generic
    }
}
