package com.example.gigachataiassistant.data.gigachat

import com.example.gigachataiassistant.data.gigachat.api.GigaChatOAuthApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class GigaChatTokenProvider(
    private val oauthApi: GigaChatOAuthApi,
    private val authorizationKeyBase64: String,
    private val scope: String = GigaChatConstants.DEFAULT_SCOPE,
) {

    private val mutex = Mutex()

    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var expiresAtEpochSeconds: Long = 0L

    suspend fun getAuthorizationBearer(): String = mutex.withLock {
        val nowSeconds = System.currentTimeMillis() / 1000
        val token = cachedAccessToken
        if (token != null && nowSeconds < expiresAtEpochSeconds - TOKEN_SKEW_SECONDS) {
            return@withLock "Bearer $token"
        }
        refreshTokenLocked()
    }

    fun invalidate() {
        cachedAccessToken = null
        expiresAtEpochSeconds = 0L
    }

    private suspend fun refreshTokenLocked(): String {
        val rqUid = UUID.randomUUID().toString()
        val basic = "Basic $authorizationKeyBase64"
        val response = oauthApi.obtainToken(
            rqUid = rqUid,
            authorization = basic,
            scope = scope,
        )
        cachedAccessToken = response.accessToken
        expiresAtEpochSeconds = response.expiresAt
        return "Bearer ${response.accessToken}"
    }

    companion object {
        private const val TOKEN_SKEW_SECONDS = 120L
    }
}