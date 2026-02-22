package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.MessengerRepository
import com.example.messenger.model.Message
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
)

class ChatViewModel(
    private val repository: MessengerRepository,
    private val chatId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState(loading = true))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    fun loadInitial() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val initial = repository.getMessages(chatId)
            _uiState.value = ChatUiState(loading = false, messages = initial)
            startPolling()
        }
    }

    fun sendTestMessage() {
        viewModelScope.launch {
            val newMessage = repository.sendTestMessage(chatId, "Тестовое сообщение")
            _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + newMessage)
        }
    }

    private fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                delay(2_500)
                val lastId = _uiState.value.messages.lastOrNull()?.id
                val updates = repository.fetchNewMessages(chatId, lastId)
                if (updates.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + updates)
                }
            }
        }
    }

    override fun onCleared() {
        pollJob?.cancel()
        super.onCleared()
    }
}
