package com.example.gigachataiassistant.data.chat

import com.example.gigachataiassistant.data.local.ChatDao
import com.example.gigachataiassistant.data.local.MessageDao
import com.example.gigachataiassistant.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
) : MessageRepository {

    override fun observeMessages(chatId: String): Flow<List<MessageEntity>> =
        messageDao.observeMessagesForChat(chatId)

    override suspend fun getMessages(chatId: String): List<MessageEntity> =
        messageDao.getMessagesForChat(chatId)

    override suspend fun insertMessage(chatId: String, role: String, content: String): String {
        val id = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
        messageDao.insert(
            MessageEntity(
                id = id,
                chatId = chatId,
                role = role,
                content = content,
                createdAt = createdAt,
            ),
        )
        chatDao.updateLastActivity(chatId, createdAt)
        return id
    }
}