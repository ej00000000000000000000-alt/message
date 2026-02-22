package com.example.messenger.model

data class Chat(
    val id: String,
    val title: String,
    val lastMessage: String,
)

data class Message(
    val id: Long,
    val chatId: String,
    val sender: String,
    val text: String,
    val timestamp: Long,
)
