package com.waiter.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.core.Result
import com.waiter.app.core.UserRole
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

    fun login(
        role: UserRole,
        username: String,
        pass: String,
        onLoginSuccess: (Int, String, Int?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.login(role, username, pass)) {
                is Result.Ok -> {
                    onLoginSuccess(result.value.userId, result.value.username, result.value.stationId)
                    _uiState.value = AuthUiState.Success
                }
                is Result.Err -> {
                    _uiState.value = AuthUiState.Error(result.error.message ?: "Unknown error")
                }
                is Result.Loading -> {}
            }
        }
    }

    fun register(
        role: UserRole,
        username: String,
        pass: String,
        fullName: String,
        phone: String = "",
        email: String = "",
        stationId: Int? = null
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(role, username, pass, fullName, phone, email, stationId)
            when (result) {
                is Result.Ok -> _uiState.value = AuthUiState.Success
                is Result.Err -> _uiState.value = AuthUiState.Error(result.error.message ?: "Unknown error")
                is Result.Loading -> {}
            }
        }
    }

    // --- НОВИЙ МЕТОД: ЗБЕРЕЖЕННЯ ТОКЕНА ---
    fun saveToken(username: String, role: UserRole, token: String) {
        viewModelScope.launch {
            // Конвертуємо enum у рядок, який очікує бекенд (з великої літери)
            val roleString = when(role) {
                UserRole.WAITER -> "Waiter"
                UserRole.COURIER -> "Courier"
                UserRole.COOK -> "Cook"
            }
            // Викликаємо репозиторій (воно піде на сервер асинхронно)
            authRepository.saveToken(username, roleString, token)
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}