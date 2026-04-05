package com.example.gigachataiassistant.data.gigachat

import com.example.gigachataiassistant.data.gigachat.api.GigaChatChatApi
import com.example.gigachataiassistant.data.gigachat.dto.ChatCompletionRequest
import com.example.gigachataiassistant.data.gigachat.dto.ChatMessageDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GigaChatRemoteDataSource(
    private val tokenProvider: GigaChatTokenProvider,
    private val chatApi: GigaChatChatApi,
) {

    suspend fun getBalance(): GigaChatBalanceOutcome = withContext(Dispatchers.IO) {
        try {
            val auth = tokenProvider.getAuthorizationBearer()
            val response = chatApi.getBalance(authorization = auth)
            val entries = response.balance.map { row ->
                GigaChatBalanceEntry(usage = row.usage, value = row.value)
            }
            GigaChatBalanceOutcome.Success(entries)
        } catch (e: Exception) {
            if (e is HttpException) {
                when (e.code()) {
                    401 -> {
                        tokenProvider.invalidate()
                        GigaChatBalanceOutcome.Failure(e)
                    }
                    403 -> GigaChatBalanceOutcome.NotAvailable
                    else -> GigaChatBalanceOutcome.Failure(e)
                }
            } else {
                GigaChatBalanceOutcome.Failure(e)
            }
        }
    }

    suspend fun sendChat(
        messages: List<ChatMessageDto>,
        model: String = GigaChatConstants.DEFAULT_CHAT_MODEL,
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val auth = tokenProvider.getAuthorizationBearer()
            val body = ChatCompletionRequest(
                model = model,
                messages = messages,
                stream = false,
            )
            val response = chatApi.chatCompletions(authorization = auth, body = body)
            val text = response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?.trim()
                .orEmpty()
            if (text.isEmpty()) {
                Result.failure(IllegalStateException("Пустой ответ ассистента"))
            } else {
                Result.success(text)
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                tokenProvider.invalidate()
            }
            Result.failure(e)
        }
    }
}
