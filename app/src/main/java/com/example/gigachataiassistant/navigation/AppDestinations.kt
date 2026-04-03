package com.example.gigachataiassistant.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination

@Serializable
data object SignupDestination

@Serializable
data object ChatsDestination

@Serializable
data class ChatDestination(val chatId: String)

@Serializable
data object ProfileDestination

@Serializable
data object ImagesDestination