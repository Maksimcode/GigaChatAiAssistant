package com.example.gigachataiassistant.ui.chat

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigachataiassistant.R
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
    @param:StringRes val errorMessageId: Int? = null,
)

class ChatViewModel(
    private val chatId: String,
    private val userId: String,
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val gigaChat: GigaChatRemoteDataSource,
    private val defaultNewChatTitle: String,
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
            chatRepository.observeChat(chatId, userId).collect { chat ->
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
                _uiState.update { it.copy(isSending = true, errorMessageId = null) }
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
                            maybeAutoRenameChatIfDefault()
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(errorMessageId = R.string.chat_error_gigachat_failed)
                            }
                        },
                    )
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(errorMessageId = R.string.chat_error_generic)
                    }
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
                _uiState.update { it.copy(isSending = true, errorMessageId = null) }
                try {
                    val dtos = messageRepository.getMessages(chatId).map { entity ->
                        ChatMessageDto(role = entity.role, content = entity.content)
                    }
                    if (dtos.isEmpty()) {
                        _uiState.update {
                            it.copy(errorMessageId = R.string.chat_error_retry_empty)
                        }
                        return@withLock
                    }
                    gigaChat.sendChat(dtos).fold(
                        onSuccess = { assistantText ->
                            messageRepository.insertMessage(
                                chatId,
                                MessageRole.ASSISTANT,
                                assistantText,
                            )
                            maybeAutoRenameChatIfDefault()
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(errorMessageId = R.string.chat_error_gigachat_failed)
                            }
                        },
                    )
                } catch (_: Exception) {
                    _uiState.update {
                        it.copy(errorMessageId = R.string.chat_error_generic)
                    }
                } finally {
                    _uiState.update { it.copy(isSending = false) }
                }
            }
        }
    }

    private suspend fun maybeAutoRenameChatIfDefault() {
        val chat = chatRepository.getChat(chatId, userId) ?: return
        if (chat.title != defaultNewChatTitle) return
        val messages = messageRepository.getMessages(chatId)
        val firstUser = messages.firstOrNull { it.role == MessageRole.USER } ?: return
        val raw = firstUser.content.trim().replace("\n", " ")
        if (raw.isEmpty()) return
        val newTitle = if (raw.length <= CHAT_TITLE_MAX_LEN) {
            raw
        } else {
            raw.take(CHAT_TITLE_MAX_LEN - 1) + "…"
        }
        chatRepository.updateChatTitle(chatId, userId, newTitle)
    }

    companion object {
        private const val CHAT_TITLE_MAX_LEN = 48
    }
}
