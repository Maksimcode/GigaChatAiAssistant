package com.example.gigachataiassistant.data.chat

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gigachataiassistant.data.local.ChatDao
import com.example.gigachataiassistant.data.local.ChatEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChatRepositoryImpl(
    private val chatDao: ChatDao,
) : ChatRepository {

    override fun observeChat(chatId: String, userId: String): Flow<ChatEntity?> =
        chatDao.observeChatById(chatId, userId)

    override fun observeChats(userId: String, searchQuery: String): Flow<PagingData<ChatEntity>> {
        val trimmed = searchQuery.trim()
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                if (trimmed.isEmpty()) {
                    chatDao.pagingSourceAll(userId)
                } else {
                    chatDao.pagingSourceByTitle(userId, trimmed)
                }
            },
        ).flow
    }

    override suspend fun createChat(userId: String, title: String): String {
        val id = UUID.randomUUID().toString()
        chatDao.insert(
            ChatEntity(
                id = id,
                userId = userId,
                title = title,
                createdAt = System.currentTimeMillis(),
            ),
        )
        return id
    }

    override suspend fun getChat(chatId: String, userId: String): ChatEntity? =
        chatDao.getChatById(chatId, userId)

    override suspend fun updateChatTitle(chatId: String, userId: String, title: String) {
        chatDao.updateTitle(chatId, userId, title)
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
