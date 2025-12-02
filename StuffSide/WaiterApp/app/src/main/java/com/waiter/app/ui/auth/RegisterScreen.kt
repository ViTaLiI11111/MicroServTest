package com.waiter.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waiter.app.core.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    role: UserRole,
    authViewModel: AuthViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current // Для Toast

    // Змінні стану
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var stationIdText by remember { mutableStateOf("") }

    val uiState by authViewModel.uiState.collectAsState()

    val roleTitle = when(role) {
        UserRole.WAITER -> "Офіціант"
        UserRole.COURIER -> "Кур'єр"
        UserRole.COOK -> "Кухар"
    }

    // --- ЛОГІКА УСПІХУ ---
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            // Показуємо повідомлення
            Toast.makeText(context, "Реєстрація успішна! Увійдіть.", Toast.LENGTH_LONG).show()
            // Повертаємось на екран логіну
            onRegisterSuccess()
            // Скидаємо стан (хоча при навігації назад VM знищиться, але для надійності)
            authViewModel.clearError()
        }
    }

    LaunchedEffect(username, password, fullName, phone, stationIdText) {
        if (uiState is AuthUiState.Error) {
            authViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Реєстрація: $roleTitle", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            // --- СПІЛЬНІ ПОЛЯ ---
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Повне ім'я") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- СПЕЦИФІЧНІ ПОЛЯ ---
            if (role == UserRole.COURIER) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (необов'язково)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (role == UserRole.COOK) {
                OutlinedTextField(
                    value = stationIdText,
                    onValueChange = { stationIdText = it },
                    label = { Text("Номер Цеху (1, 2...)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- ПРОДОВЖЕННЯ СПІЛЬНИХ ПОЛІВ ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логін (Username)") },
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
                    val sId = if (role == UserRole.COOK) stationIdText.toIntOrNull() else null
                    // Тут важливо: Register у AuthViewModel має ставити стан Success при успіху
                    authViewModel.register(
                        role,
                        username,
                        password,
                        fullName,
                        phone,
                        email,
                        sId
                    )
                },
                enabled = uiState !is AuthUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState is AuthUiState.Loading) "Реєстрація..." else "Зареєструватись")
            }
        }
    }
}