package com.example.gigachataiassistant.ui.auth

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.gigachataiassistant.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun rememberGoogleSignInLauncher(
    onSuccess: (String) -> Unit,
    onDismissed: () -> Unit,
    onError: (Throwable) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    return remember(credentialManager, activity, scope) {
        {
            val webId = BuildConfig.FIREBASE_WEB_CLIENT_ID
            if (webId.isBlank()) {
                onError(IllegalStateException("firebase.web.client.id"))
                return@remember
            }
            val act = activity ?: run {
                onError(IllegalStateException("Context is not ComponentActivity"))
                return@remember
            }
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        context = act,
                        request = request,
                    )
                    val credential = result.credential
                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        if (idToken.isNotBlank()) {
                            onSuccess(idToken)
                        } else {
                            onError(IllegalStateException("idToken is empty"))
                        }
                    } else {
                        onError(IllegalStateException("Unexpected credential type"))
                    }
                } catch (_: GetCredentialCancellationException) {
                    onDismissed()
                } catch (e: GetCredentialException) {
                    onError(e)
                } catch (e: Exception) {
                    onError(e)
                }
            }
        }
    }
}
