package com.example.gigachataiassistant.data.gigachat.api

import com.example.gigachataiassistant.data.gigachat.dto.BalanceResponseDto
import com.example.gigachataiassistant.data.gigachat.dto.ChatCompletionRequest
import com.example.gigachataiassistant.data.gigachat.dto.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaChatChatApi {

    @GET("api/v1/balance")
    suspend fun getBalance(
        @Header("Authorization") authorization: String,
    ): BalanceResponseDto

    @POST("api/v1/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body body: ChatCompletionRequest,
    ): ChatCompletionResponse
}