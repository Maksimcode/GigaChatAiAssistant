package com.example.gigachataiassistant.domain.auth

sealed class AuthError {
    data object EmptyFields : AuthError()
    data object InvalidEmail : AuthError()
    data object WrongPassword : AuthError()
    data object UserNotFound : AuthError()
    data object EmailAlreadyInUse : AuthError()
    data object WeakPassword : AuthError()
    data object Network : AuthError()
    data object InvalidCredential : AuthError()
    data object StorageUpload : AuthError()
    data object Unknown : AuthError()
}