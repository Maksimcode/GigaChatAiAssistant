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

    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
    fun observeChatById(id: String): Flow<ChatEntity?>

    @Query("SELECT * FROM chats ORDER BY createdAt DESC")
    fun pagingSourceAll(): PagingSource<Int, ChatEntity>

    @Query(
        "SELECT * FROM chats WHERE title LIKE '%' || :query || '%' COLLATE NOCASE ORDER BY createdAt DESC",
    )
    fun pagingSourceByTitle(query: String): PagingSource<Int, ChatEntity>
}