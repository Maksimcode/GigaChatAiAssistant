package com.example.gigachataiassistant.domain.auth

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signUpWithEmail(email: String, password: String): AuthResult
    fun getCurrentUser(): com.google.firebase.auth.FirebaseUser?
    fun signOut()
}