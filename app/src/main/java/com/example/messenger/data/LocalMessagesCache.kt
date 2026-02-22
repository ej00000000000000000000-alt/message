package com.example.messenger.data

import android.content.Context
import com.example.messenger.model.Message
import org.json.JSONArray
import org.json.JSONObject

class LocalMessagesCache(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun read(chatId: String): List<Message>? {
        val raw = prefs.getString(chatId, null) ?: return null
        return runCatching {
            val jsonArray = JSONArray(raw)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(index)
                    add(
                        Message(
                            id = item.getLong(KEY_ID),
                            chatId = item.getString(KEY_CHAT_ID),
                            sender = item.getString(KEY_SENDER),
                            text = item.getString(KEY_TEXT),
                            timestamp = item.getLong(KEY_TIMESTAMP),
                        )
                    )
                }
            }
        }.getOrNull()
    }

    fun save(chatId: String, messages: List<Message>) {
        val payload = JSONArray().apply {
            messages.forEach { message ->
                put(
                    JSONObject().apply {
                        put(KEY_ID, message.id)
                        put(KEY_CHAT_ID, message.chatId)
                        put(KEY_SENDER, message.sender)
                        put(KEY_TEXT, message.text)
                        put(KEY_TIMESTAMP, message.timestamp)
                    }
                )
            }
        }

        prefs.edit().putString(chatId, payload.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "chat_messages_cache"
        private const val KEY_ID = "id"
        private const val KEY_CHAT_ID = "chatId"
        private const val KEY_SENDER = "sender"
        private const val KEY_TEXT = "text"
        private const val KEY_TIMESTAMP = "timestamp"
    }
}
