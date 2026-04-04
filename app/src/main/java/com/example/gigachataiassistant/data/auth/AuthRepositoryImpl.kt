package com.example.gigachataiassistant.data.auth

import com.example.gigachataiassistant.domain.auth.AuthError
import com.example.gigachataiassistant.domain.auth.AuthRepository
import com.example.gigachataiassistant.domain.auth.AuthResult
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): AuthResult = try {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
        AuthResult.Success
    } catch (e: Throwable) {
        AuthResult.Failure(mapThrowable(e))
    }

    override suspend fun signUpWithEmail(email: String, password: String): AuthResult = try {
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
        AuthResult.Success
    } catch (e: Throwable) {
        AuthResult.Failure(mapThrowable(e))
    }

    override fun getCurrentUser(): com.google.firebase.auth.FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun mapThrowable(t: Throwable): AuthError = when (t) {
        is FirebaseNetworkException -> AuthError.Network
        is FirebaseAuthException -> mapFirebaseAuthError(t.errorCode)
        else -> AuthError.Unknown
    }

    private fun mapFirebaseAuthError(code: String): AuthError = when (code) {
        "ERROR_INVALID_EMAIL" -> AuthError.InvalidEmail
        "ERROR_WRONG_PASSWORD" -> AuthError.WrongPassword
        "ERROR_USER_NOT_FOUND" -> AuthError.UserNotFound
        "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EmailAlreadyInUse
        "ERROR_WEAK_PASSWORD" -> AuthError.WeakPassword
        "ERROR_INVALID_CREDENTIAL" -> AuthError.InvalidCredential
        else -> AuthError.Unknown
    }
}