package com.example.gigachataiassistant.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gigachataiassistant.data.chat.ChatRepository
import com.example.gigachataiassistant.data.chat.MessageRepository
import com.example.gigachataiassistant.data.gigachat.GigaChatRemoteDataSource

class ChatViewModelFactory(
    private val chatId: String,
    private val userId: String,
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val gigaChat: GigaChatRemoteDataSource,
    private val defaultNewChatTitle: String,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                chatId = chatId,
                userId = userId,
                messageRepository = messageRepository,
                chatRepository = chatRepository,
                gigaChat = gigaChat,
                defaultNewChatTitle = defaultNewChatTitle,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
