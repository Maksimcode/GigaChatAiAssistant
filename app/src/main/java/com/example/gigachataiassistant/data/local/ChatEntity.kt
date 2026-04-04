package com.example.gigachataiassistant.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chats",
    indices = [Index(value = ["title"])],
)
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
)