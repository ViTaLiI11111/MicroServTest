package com.waiter.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit // <--- НОВИЙ ПАРАМЕТР
) {
    val username by vm.usernameFlow.collectAsState(initial = "Waiter")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Налаштування") },
                // --- ДОДАНО КНОПКУ НАЗАД ---
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
                // ---------------------------
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {

            Text("Ви увійшли як: $username", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Вийти з акаунту")
            }
        }
    }
}