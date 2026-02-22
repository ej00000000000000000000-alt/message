package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.MessengerRepository
import com.example.messenger.model.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatListUiState(
    val loading: Boolean = false,
    val chats: List<Chat> = emptyList(),
)

class ChatListViewModel(
    private val repository: MessengerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState(loading = true))
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    fun loadChats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val chats = repository.getChats()
            _uiState.value = ChatListUiState(loading = false, chats = chats)
        }
    }
}
