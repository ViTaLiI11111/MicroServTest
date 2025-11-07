package com.waiter.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    onLogout: () -> Unit // <-- Новий параметр для виходу
) {
    // Ми можемо отримувати ім'я юзера з vm
    val username by vm.usernameFlow.collectAsState(initial = "Waiter")

    // Старий код для waiterId (можна видалити, якщо більше не потрібен)
    // val current by vm.waiterIdFlow.collectAsState(initial = "waiter-001")
    // var waiterId by remember(current) { mutableStateOf(current) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Налаштування") }) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {

            Text("Ви увійшли як: $username", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(32.dp))

            // Старий функціонал (можна видалити)
            /*
            OutlinedTextField(
                value = waiterId,
                onValueChange = { waiterId = it },
                label = { Text("Waiter ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { vm.saveWaiterId(waiterId) }) {
                Text("Зберегти ID")
            }
            */
            // Кінець старого функціоналу

            // Нова кнопка "Вийти"
            Button(
                onClick = onLogout, // Викликаємо callback
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Вийти")
            }
        }
    }
}