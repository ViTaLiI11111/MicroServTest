package com.waiter.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.core.Result
import com.waiter.app.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Стан UI для екранів автентифікації
sealed interface AuthUiState {
    object Idle : AuthUiState // Початковий стан
    object Loading : AuthUiState
    object Success : AuthUiState // Успішний вхід або реєстрація
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(username: String, password: String, onLoginSuccess: (Int, String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.login(username, password)) {
                is Result.Ok -> {
                    // Викликаємо callback, щоб SettingsViewModel зберіг сесію
                    onLoginSuccess(result.value.userId, result.value.username)
                    _uiState.value = AuthUiState.Success
                }
                is Result.Err -> {
                    _uiState.value = AuthUiState.Error(result.error.message ?: "Unknown error")
                }
                is Result.Loading -> {}
            }
        }
    }

    fun register(username: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.register(username, password, fullName)) {
                is Result.Ok -> {
                    // Після реєстрації одразу переводимо на екран логіну
                    _uiState.value = AuthUiState.Idle
                    // Тут можна показати Snackbar "Registration successful"
                }
                is Result.Err -> {
                    _uiState.value = AuthUiState.Error(result.error.message ?: "Unknown error")
                }
                is Result.Loading -> {}
            }
        }
    }

    // Скидання стану помилки
    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}