package com.example.gigachataiassistant.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatEntity)

    @Query("SELECT * FROM chats WHERE userId = :userId ORDER BY lastActivityAt DESC")
    fun pagingSourceAll(userId: String): PagingSource<Int, ChatEntity>

    @Query(
        "SELECT * FROM chats WHERE userId = :userId AND title LIKE '%' || :query || '%' COLLATE NOCASE ORDER BY lastActivityAt DESC",
    )
    fun pagingSourceByTitle(userId: String, query: String): PagingSource<Int, ChatEntity>

    @Query("SELECT * FROM chats WHERE id = :id AND userId = :userId LIMIT 1")
    fun observeChatById(id: String, userId: String): Flow<ChatEntity?>

    @Query("SELECT * FROM chats WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getChatById(id: String, userId: String): ChatEntity?

    @Query("UPDATE chats SET title = :title WHERE id = :chatId AND userId = :userId")
    suspend fun updateTitle(chatId: String, userId: String, title: String)

    @Query("UPDATE chats SET lastActivityAt = :timeMillis WHERE id = :chatId")
    suspend fun updateLastActivity(chatId: String, timeMillis: Long)

    @Query("DELETE FROM chats WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("DELETE FROM chats WHERE id = :chatId AND userId = :userId")
    suspend fun deleteById(chatId: String, userId: String)
}
