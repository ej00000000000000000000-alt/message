package com.example.messenger.data

import com.example.messenger.model.Chat
import com.example.messenger.model.Message

interface MessengerRepository {
    suspend fun getChats(): List<Chat>
    suspend fun getMessages(chatId: String): List<Message>
    suspend fun fetchNewMessages(chatId: String, afterMessageId: Long?): List<Message>
    suspend fun sendTestMessage(chatId: String, text: String): Message
}
