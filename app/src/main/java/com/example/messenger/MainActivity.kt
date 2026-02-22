package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messenger.data.FakeMessengerRepository
import com.example.messenger.data.LocalMessagesCache
import com.example.messenger.data.SecureCredentialsStorage
import com.example.messenger.model.Chat
import com.example.messenger.ui.ChatListScreen
import com.example.messenger.ui.ChatScreen
import com.example.messenger.ui.LoginScreen
import com.example.messenger.viewmodel.AppViewModel
import com.example.messenger.viewmodel.ChatListViewModel
import com.example.messenger.viewmodel.ChatViewModel

private sealed interface Screen {
    data object Login : Screen
    data object ChatList : Screen
    data class ChatDetails(val chat: Chat) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel(factory = SimpleFactory {
                AppViewModel(
                    repository = FakeMessengerRepository(LocalMessagesCache(applicationContext)),
                    credentialsStorage = SecureCredentialsStorage(applicationContext),
                )
            })
            var currentScreen: Screen by remember {
                mutableStateOf(if (appViewModel.isLoggedIn()) Screen.ChatList else Screen.Login)
            }

            when (val screen = currentScreen) {
                Screen.Login -> {
                    LoginScreen(onLogin = { username, password ->
                        if (appViewModel.login(username, password)) {
                            currentScreen = Screen.ChatList
                        }
                    })
                }

                Screen.ChatList -> {
                    val vm: ChatListViewModel = viewModel(factory = SimpleFactory {
                        ChatListViewModel(appViewModel.repository)
                    })
                    val state by vm.uiState.collectAsState()

                    LaunchedEffect(Unit) {
                        vm.loadChats()
                    }

                    ChatListScreen(
                        loading = state.loading,
                        chats = state.chats,
                        onChatClick = { currentScreen = Screen.ChatDetails(it) },
                        onLogout = {
                            appViewModel.logout()
                            currentScreen = Screen.Login
                        },
                    )
                }

                is Screen.ChatDetails -> {
                    val vm: ChatViewModel = viewModel(key = screen.chat.id, factory = SimpleFactory {
                        ChatViewModel(appViewModel.repository, screen.chat.id)
                    })
                    val state by vm.uiState.collectAsState()

                    LaunchedEffect(screen.chat.id) {
                        vm.loadInitial()
                    }

                    ChatScreen(
                        title = screen.chat.title,
                        loading = state.loading,
                        messages = state.messages,
                        onBack = { currentScreen = Screen.ChatList },
                        onSendTest = vm::sendTestMessage,
                    )
                }
            }
        }
    }
}
