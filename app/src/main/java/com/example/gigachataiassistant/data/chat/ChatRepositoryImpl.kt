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

    override fun observeChat(chatId: String): Flow<ChatEntity?> =
        chatDao.observeChatById(chatId)

    override fun observeChats(searchQuery: String): Flow<PagingData<ChatEntity>> {
        val trimmed = searchQuery.trim()
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                if (trimmed.isEmpty()) {
                    chatDao.pagingSourceAll()
                } else {
                    chatDao.pagingSourceByTitle(trimmed)
                }
            },
        ).flow
    }

    override suspend fun createChat(title: String): String {
        val id = UUID.randomUUID().toString()
        chatDao.insert(
            ChatEntity(
                id = id,
                title = title,
                createdAt = System.currentTimeMillis(),
            ),
        )
        return id
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}