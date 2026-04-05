package com.example.gigachataiassistant.domain.auth

import android.net.Uri

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun signUpWithEmail(email: String, password: String): AuthResult
    fun getCurrentUser(): com.google.firebase.auth.FirebaseUser?
    suspend fun updateProfile(displayName: String): AuthResult
    suspend fun updateProfilePhoto(localImageUri: Uri): AuthResult
    fun signOut()
}