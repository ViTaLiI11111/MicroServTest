package com.waiter.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waiter.app.core.UserRole // Імпорт Enum

@Composable
fun LoginScreen(
    role: UserRole, // <-- НОВИЙ ПАРАМЕТР
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccessSaveSession: (Int, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    // Визначаємо текст заголовка залежно від ролі
    val roleTitle = if (role == UserRole.WAITER) "Офіціант" else "Кур'єр"

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Динамічний заголовок
        Text("Вхід: $roleTitle", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логін") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState is AuthUiState.Error) {
            Text(
                text = (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                // Передаємо роль у ViewModel
                authViewModel.login(role, username, password, onLoginSuccessSaveSession)
            },
            enabled = uiState !is AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState is AuthUiState.Loading) "Вхід..." else "Увійти")
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Реєстрація")
        }
    }
}