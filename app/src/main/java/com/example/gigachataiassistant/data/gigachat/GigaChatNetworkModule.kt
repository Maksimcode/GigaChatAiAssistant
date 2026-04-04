package com.example.gigachataiassistant.data.gigachat

import com.example.gigachataiassistant.BuildConfig
import com.example.gigachataiassistant.data.gigachat.api.GigaChatChatApi
import com.example.gigachataiassistant.data.gigachat.api.GigaChatOAuthApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object GigaChatNetworkModule {

    fun createRemoteDataSource(): GigaChatRemoteDataSource {
        val authKey = BuildConfig.GIGACHAT_AUTH_KEY.trim()
        val json = createJson()
        val client = createOkHttpClient()
        val contentType = "application/json".toMediaType()
        val converter = json.asConverterFactory(contentType)

        val oauthRetrofit = Retrofit.Builder()
            .baseUrl(GigaChatConstants.OAUTH_BASE_URL)
            .client(client)
            .addConverterFactory(converter)
            .build()

        val chatRetrofit = Retrofit.Builder()
            .baseUrl(GigaChatConstants.CHAT_API_BASE_URL)
            .client(client)
            .addConverterFactory(converter)
            .build()

        val oauthApi = oauthRetrofit.create(GigaChatOAuthApi::class.java)
        val chatApi = chatRetrofit.create(GigaChatChatApi::class.java)
        val tokenProvider = GigaChatTokenProvider(
            oauthApi = oauthApi,
            authorizationKeyBase64 = authKey,
        )
        return GigaChatRemoteDataSource(
            tokenProvider = tokenProvider,
            chatApi = chatApi,
        )
    }

    private fun createJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                redactHeader("Authorization")
            }
            builder.addInterceptor(logging)
        }
        return builder.build()
    }
}
