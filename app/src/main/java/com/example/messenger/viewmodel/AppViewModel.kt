package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import com.example.messenger.data.FakeMessengerRepository
import com.example.messenger.data.MessengerRepository
import com.example.messenger.data.SecureCredentialsStorage
import com.example.messenger.data.UserCredentials

class AppViewModel(
    val repository: MessengerRepository = FakeMessengerRepository(),
    private val credentialsStorage: SecureCredentialsStorage,
) : ViewModel() {

    fun isLoggedIn(): Boolean = credentialsStorage.read() != null

    fun login(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) return false

        // In real app token would be returned by backend.
        val fakeToken = "token_${username.trim()}"
        credentialsStorage.save(
            UserCredentials(
                username = username.trim(),
                token = fakeToken,
            )
        )
        return true
    }

    fun logout() {
        credentialsStorage.clear()
    }
}
