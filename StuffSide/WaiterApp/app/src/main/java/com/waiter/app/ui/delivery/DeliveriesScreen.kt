package com.waiter.app.ui.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    courierId: Int, // Отримуємо ID кур'єра
    vm: DeliveriesViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val available by vm.available.collectAsState()
    val myDeliveries by vm.myDeliveries.collectAsState()
    val error by vm.error.collectAsState()

    // Завантажуємо дані при вході
    LaunchedEffect(courierId) {
        vm.loadData(courierId)
    }

    // Показуємо помилку якщо є
    LaunchedEffect(error) {
        if (error != null) {
            // Тут можна показати Snackbar, поки просто лог або ігнор
            vm.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Кабінет Кур'єра") },
                actions = {
                    IconButton(onClick = { vm.loadData(courierId) }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Exit")
                    }
                }
            )
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).padding(16.dp)) {

            // Секція: Мої активні замовлення
            if (myDeliveries.isNotEmpty()) {
                item {
                    Text("Мої активні замовлення", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                }
                items(myDeliveries) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Світло-зелений
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Доставка #${item.id} (Статус: ${item.status})", style = MaterialTheme.typography.titleSmall)
                            Text("Адреса: ${item.clientAddress}")
                            Text("Телефон: ${item.clientPhone ?: "-"}")
                        }
                    }
                }
                item { Divider(Modifier.padding(vertical = 16.dp)) }
            }

            // Секція: Доступні замовлення
            item {
                Text("Вільні замовлення", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }

            if (available.isEmpty()) {
                item { Text("Немає вільних замовлень", color = Color.Gray) }
            }

            items(available) { item ->
                Card(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Замовлення #${item.orderId}", style = MaterialTheme.typography.titleSmall)
                        Text("Куди: ${item.clientAddress}")
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { vm.takeOrder(item.id, courierId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Взяти в роботу")
                        }
                    }
                }
            }
        }
    }
}