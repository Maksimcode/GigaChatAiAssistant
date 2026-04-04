package com.example.gigachataiassistant.data.chat

import com.example.gigachataiassistant.data.local.MessageDao
import com.example.gigachataiassistant.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
) : MessageRepository {

    override fun observeMessages(chatId: String): Flow<List<MessageEntity>> =
        messageDao.observeMessagesForChat(chatId)

    override suspend fun getMessages(chatId: String): List<MessageEntity> =
        messageDao.getMessagesForChat(chatId)

    override suspend fun insertMessage(chatId: String, role: String, content: String): String {
        val id = UUID.randomUUID().toString()
        messageDao.insert(
            MessageEntity(
                id = id,
                chatId = chatId,
                role = role,
                content = content,
                createdAt = System.currentTimeMillis(),
            ),
        )
        return id
    }
}