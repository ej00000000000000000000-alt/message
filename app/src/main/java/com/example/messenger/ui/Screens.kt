package com.example.messenger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.messenger.model.Chat
import com.example.messenger.model.Message

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var username by remember { mutableStateOf("demo_user") }
    var password by remember { mutableStateOf("password") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Simple Messenger", style = MaterialTheme.typography.headlineMedium)
            Text("Вход c сохранением данных в защищенном хранилище", modifier = Modifier.padding(top = 12.dp, bottom = 24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 18.dp),
            )

            Button(onClick = { onLogin(username, password) }, modifier = Modifier.fillMaxWidth()) {
                Text("Войти")
            }
        }
    }
}

@Composable
fun ChatListScreen(
    loading: Boolean,
    chats: List<Chat>,
    onChatClick: (Chat) -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Чаты", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onLogout) { Text("Выйти") }
            }
        }
    ) { padding ->
        if (loading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(chats) { chat ->
                    Card(onClick = { onChatClick(chat) }, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(chat.title, fontWeight = FontWeight.Bold)
                            Text(chat.lastMessage, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    title: String,
    loading: Boolean,
    messages: List<Message>,
    onBack: () -> Unit,
    onSendTest: () -> Unit,
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onBack) { Text("Назад") }
                Text(title, fontWeight = FontWeight.Bold)
                Button(onClick = onSendTest) { Text("Тест +") }
            }
        },
    ) { padding ->
        if (loading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages) { msg ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(msg.sender, fontWeight = FontWeight.SemiBold)
                            Text(msg.text, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
    }
}
