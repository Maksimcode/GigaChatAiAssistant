package com.example.gigachataiassistant.data.chat

import androidx.paging.PagingData
import com.example.gigachataiassistant.data.local.ChatEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun observeChats(searchQuery: String): Flow<PagingData<ChatEntity>>

    suspend fun createChat(title: String): String
}