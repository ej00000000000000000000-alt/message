package com.example.messenger.data

import com.example.messenger.model.Chat
import com.example.messenger.model.Message
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicLong

class FakeMessengerRepository(
    private val localMessagesCache: LocalMessagesCache? = null,
) : MessengerRepository {
    private val messageIdCounter = AtomicLong(100)

    private val chats = listOf(
        Chat(id = "chat_1", title = "Android Team", lastMessage = "Daily at 11:00"),
        Chat(id = "chat_2", title = "Design", lastMessage = "Новый макет загружен"),
        Chat(id = "chat_3", title = "QA", lastMessage = "Релиз прошёл smoke-тест"),
    )

    private val messagesByChat = mutableMapOf(
        "chat_1" to mutableListOf(
            Message(1, "chat_1", "Alice", "Привет!", System.currentTimeMillis() - 50_000),
            Message(2, "chat_1", "Bob", "Готовим демо", System.currentTimeMillis() - 20_000),
        ),
        "chat_2" to mutableListOf(
            Message(3, "chat_2", "Nina", "Смотрите обновленный UI", System.currentTimeMillis() - 100_000),
        ),
        "chat_3" to mutableListOf(
            Message(4, "chat_3", "Ivan", "Все тесты прошли", System.currentTimeMillis() - 70_000),
        ),
    )

    init {
        hydrateFromCache()
    }

    override suspend fun getChats(): List<Chat> {
        delay(350)
        return chats
    }

    override suspend fun getMessages(chatId: String): List<Message> {
        delay(250)
        val cached = localMessagesCache?.read(chatId)
        if (cached != null) {
            messagesByChat[chatId] = cached.toMutableList()
        }
        return messagesByChat[chatId]?.toList().orEmpty()
    }

    override suspend fun fetchNewMessages(chatId: String, afterMessageId: Long?): List<Message> {
        delay(300)
        maybeGenerateIncomingMessage(chatId)
        val existing = messagesByChat[chatId].orEmpty()
        return if (afterMessageId == null) {
            existing.toList()
        } else {
            existing.filter { it.id > afterMessageId }
        }
    }

    override suspend fun sendTestMessage(chatId: String, text: String): Message {
        delay(200)
        val message = Message(
            id = messageIdCounter.incrementAndGet(),
            chatId = chatId,
            sender = "Me",
            text = text,
            timestamp = System.currentTimeMillis(),
        )
        messagesByChat.getOrPut(chatId) { mutableListOf() }.add(message)
        persist(chatId)
        return message
    }

    private fun maybeGenerateIncomingMessage(chatId: String) {
        val shouldGenerate = (0..100).random() > 65
        if (!shouldGenerate) return

        val incoming = Message(
            id = messageIdCounter.incrementAndGet(),
            chatId = chatId,
            sender = "ServerBot",
            text = "Новый входящий ping #${messageIdCounter.get()}",
            timestamp = System.currentTimeMillis(),
        )

        messagesByChat.getOrPut(chatId) { mutableListOf() }.add(incoming)
        persist(chatId)
    }

    private fun hydrateFromCache() {
        chats.forEach { chat ->
            val cachedMessages = localMessagesCache?.read(chat.id) ?: return@forEach
            messagesByChat[chat.id] = cachedMessages.toMutableList()
            val maxId = cachedMessages.maxOfOrNull { it.id } ?: return@forEach
            messageIdCounter.updateAndGet { current -> maxOf(current, maxId) }
        }
    }

    private fun persist(chatId: String) {
        val messages = messagesByChat[chatId]?.toList().orEmpty()
        localMessagesCache?.save(chatId, messages)
    }
}
