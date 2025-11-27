package com.waiter.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.core.Result
import com.waiter.app.core.UserRole // Імпорт Enum
import com.waiter.app.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Додано аргумент role
    fun login(role: UserRole, username: String, pass: String, onLoginSuccess: (Int, String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // Передаємо роль у репозиторій
            when (val result = authRepository.login(role, username, pass)) {
                is Result.Ok -> {
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

    // Додано аргумент role
    fun register(
        role: UserRole,
        username: String,
        pass: String,
        fullName: String,
        phone: String = "",
        email: String = "" // <-- ДОДАНО (дефолт пустий)
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // Передаємо всі дані далі
            val result = authRepository.register(role, username, pass, fullName, phone, email)

            when (result) {
                is Result.Ok -> {
                    _uiState.value = AuthUiState.Idle
                }
                is Result.Err -> {
                    _uiState.value = AuthUiState.Error(result.error.message ?: "Unknown error")
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}