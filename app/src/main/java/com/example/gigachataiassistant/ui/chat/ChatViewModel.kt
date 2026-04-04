package com.example.gigachataiassistant.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigachataiassistant.data.chat.ChatRepository
import com.example.gigachataiassistant.data.chat.MessageRepository
import com.example.gigachataiassistant.data.gigachat.GigaChatRemoteDataSource
import com.example.gigachataiassistant.data.gigachat.dto.ChatMessageDto
import com.example.gigachataiassistant.data.local.MessageEntity
import com.example.gigachataiassistant.data.local.MessageRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val title: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
)

class ChatViewModel(
    private val chatId: String,
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val gigaChat: GigaChatRemoteDataSource,
) : ViewModel() {

    private val sendMutex = Mutex()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            messageRepository.observeMessages(chatId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
        viewModelScope.launch {
            chatRepository.observeChat(chatId).collect { chat ->
                _uiState.update { it.copy(title = chat?.title.orEmpty()) }
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val trimmed = text.trim()
            if (trimmed.isEmpty()) return@launch
            sendMutex.withLock {
                if (_uiState.value.isSending) return@withLock
                _uiState.update { it.copy(isSending = true, error = null) }
                try {
                    messageRepository.insertMessage(chatId, MessageRole.USER, trimmed)
                    val dtos = messageRepository.getMessages(chatId).map { entity ->
                        ChatMessageDto(role = entity.role, content = entity.content)
                    }
                    gigaChat.sendChat(dtos).fold(
                        onSuccess = { assistantText ->
                            messageRepository.insertMessage(
                                chatId,
                                MessageRole.ASSISTANT,
                                assistantText,
                            )
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(error = e.message ?: "Не удалось получить ответ")
                            }
                        },
                    )
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Ошибка") }
                } finally {
                    _uiState.update { it.copy(isSending = false) }
                }
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            sendMutex.withLock {
                if (_uiState.value.isSending) return@withLock
                _uiState.update { it.copy(isSending = true, error = null) }
                try {
                    val dtos = messageRepository.getMessages(chatId).map { entity ->
                        ChatMessageDto(role = entity.role, content = entity.content)
                    }
                    if (dtos.isEmpty()) {
                        _uiState.update { it.copy(error = "Нет сообщений для повтора") }
                        return@withLock
                    }
                    gigaChat.sendChat(dtos).fold(
                        onSuccess = { assistantText ->
                            messageRepository.insertMessage(
                                chatId,
                                MessageRole.ASSISTANT,
                                assistantText,
                            )
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(error = e.message ?: "Не удалось получить ответ")
                            }
                        },
                    )
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Ошибка") }
                } finally {
                    _uiState.update { it.copy(isSending = false) }
                }
            }
        }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}