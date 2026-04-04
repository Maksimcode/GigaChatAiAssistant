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

    @Query("SELECT * FROM chats WHERE userId = :userId ORDER BY createdAt DESC")
    fun pagingSourceAll(userId: String): PagingSource<Int, ChatEntity>

    @Query(
        "SELECT * FROM chats WHERE userId = :userId AND title LIKE '%' || :query || '%' COLLATE NOCASE ORDER BY createdAt DESC",
    )
    fun pagingSourceByTitle(userId: String, query: String): PagingSource<Int, ChatEntity>

    @Query("SELECT * FROM chats WHERE id = :id AND userId = :userId LIMIT 1")
    fun observeChatById(id: String, userId: String): Flow<ChatEntity?>
}
