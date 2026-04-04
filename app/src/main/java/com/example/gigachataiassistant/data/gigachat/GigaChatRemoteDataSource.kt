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
