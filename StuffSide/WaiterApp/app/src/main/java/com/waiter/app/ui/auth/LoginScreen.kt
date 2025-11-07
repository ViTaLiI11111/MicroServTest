package com.waiter.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccessSaveSession: (Int, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    // Ми отримаємо SettingsViewModel з Activity/NavGraph,
    // щоб зберегти сесію після успішного логіну
    // !! ВАЖЛИВО: це буде передано з NavGraph
    // val settingsViewModel: SettingsViewModel = ...

    // Цей код буде в NavGraph:
    // val settingsViewModel: SettingsViewModel = viewModel(viewModelStoreOwner = activity)
    // val authViewModel: AuthViewModel = viewModel()
    // ...
    // LoginScreen(
    //   authViewModel = authViewModel,
    //   onLoginSuccess = {
    //      navController.navigate(Routes.LIST) { popUpTo(Routes.AUTH_GRAPH) { inclusive = true } }
    //   },
    //   onLoginSuccessSaveSession = { id, name -> settingsViewModel.saveLoginSession(id, name) },
    //   onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
    // )

    // *** Поки що ми імплементуємо сам екран, а логіку onLoginSuccessSaveSession
    // *** передамо в наступному кроці з NavGraph.



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
        Text("Вхід для офіціанта", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логін") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState is AuthUiState.Error) {
            Text((uiState as AuthUiState.Error).message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { authViewModel.login(username, password, onLoginSuccessSaveSession) },
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