package com.example.gigachataiassistant.data.gigachat.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val stream: Boolean = false,
)

@Serializable
data class ChatMessageDto(
    val role: String,
    val content: String,
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatChoiceDto> = emptyList(),
    @SerialName("object") val objectType: String? = null,
)

@Serializable
data class ChatChoiceDto(
    val index: Int? = null,
    val message: ChatMessageDto? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)