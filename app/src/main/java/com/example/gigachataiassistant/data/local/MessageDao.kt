package com.example.gigachataiassistant.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Query(
        "SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC",
    )
    fun observeMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query(
        "SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC",
    )
    suspend fun getMessagesForChat(chatId: String): List<MessageEntity>
}