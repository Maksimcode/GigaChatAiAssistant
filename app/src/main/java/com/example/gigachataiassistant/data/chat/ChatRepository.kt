package com.example.gigachataiassistant.data.chat

import androidx.paging.PagingData
import com.example.gigachataiassistant.data.local.ChatEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun observeChat(chatId: String, userId: String): Flow<ChatEntity?>

    fun observeChats(userId: String, searchQuery: String): Flow<PagingData<ChatEntity>>

    suspend fun createChat(userId: String, title: String): String

    suspend fun getChat(chatId: String, userId: String): ChatEntity?

    suspend fun updateChatTitle(chatId: String, userId: String, title: String)

    suspend fun deleteAllChatsForUser(userId: String)

    suspend fun deleteChat(chatId: String, userId: String)
}
