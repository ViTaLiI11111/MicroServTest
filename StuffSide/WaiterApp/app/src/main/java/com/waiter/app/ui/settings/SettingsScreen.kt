package com.waiter.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val current by vm.waiterIdFlow.collectAsState(initial = "waiter-001")
    var waiterId by remember(current) { mutableStateOf(current) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Налаштування") }) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            OutlinedTextField(
                value = waiterId,
                onValueChange = { waiterId = it },
                label = { Text("Waiter ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { vm.saveWaiterId(waiterId) }) {
                Text("Зберегти")
            }
        }
    }
}
