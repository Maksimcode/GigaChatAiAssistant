package com.example.gigachataiassistant.data.chat

import com.example.gigachataiassistant.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun observeMessages(chatId: String): Flow<List<MessageEntity>>

    suspend fun getMessages(chatId: String): List<MessageEntity>

    suspend fun insertMessage(chatId: String, role: String, content: String): String
}