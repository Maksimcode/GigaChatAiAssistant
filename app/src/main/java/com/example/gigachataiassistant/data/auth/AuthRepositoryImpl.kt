package com.example.gigachataiassistant.data.auth

import android.content.Context
import android.net.Uri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.gigachataiassistant.BuildConfig
import com.example.gigachataiassistant.domain.auth.AuthError
import com.example.gigachataiassistant.domain.auth.AuthRepository
import com.example.gigachataiassistant.domain.auth.AuthResult
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

class AuthRepositoryImpl(
    private val appContext: Context,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthRepository {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun signInWithEmail(email: String, password: String): AuthResult = try {
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
        AuthResult.Success
    } catch (e: Throwable) {
        AuthResult.Failure(mapThrowable(e))
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
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

    override suspend fun updateProfile(displayName: String): AuthResult = try {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
        val request = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(request).await()
        user.reload().await()
        AuthResult.Success
    } catch (e: Throwable) {
        AuthResult.Failure(mapThrowable(e))
    }

    override suspend fun updateProfilePhoto(localImageUri: Uri): AuthResult = try {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("User not logged in")
        val ref = FirebaseStorage.getInstance().reference.child("users/${user.uid}/avatar.jpg")
        ref.putFile(localImageUri).await()
        val downloadUrl = ref.downloadUrl.await()
        val request = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setPhotoUri(downloadUrl.toString().toUri())
            .build()
        user.updateProfile(request).await()
        user.reload().await()
        AuthResult.Success
    } catch (e: Throwable) {
        AuthResult.Failure(mapThrowable(e))
    }

    override fun signOut() {
        firebaseAuth.signOut()
        if (BuildConfig.FIREBASE_WEB_CLIENT_ID.isNotBlank()) {
            val credentialManager = CredentialManager.create(appContext)
            ioScope.launch {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun mapThrowable(t: Throwable): AuthError = when (t) {
        is FirebaseNetworkException -> AuthError.Network
        is StorageException -> AuthError.StorageUpload
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