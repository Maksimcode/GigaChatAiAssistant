package com.example.gigachataiassistant.data.gigachat.api

import com.example.gigachataiassistant.data.gigachat.dto.OAuthTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaChatOAuthApi {

    @POST("api/v2/oauth")
    @FormUrlEncoded
    suspend fun obtainToken(
        @Header("RqUID") rqUid: String,
        @Header("Authorization") authorization: String,
        @Field("scope") scope: String,
    ): OAuthTokenResponse
}