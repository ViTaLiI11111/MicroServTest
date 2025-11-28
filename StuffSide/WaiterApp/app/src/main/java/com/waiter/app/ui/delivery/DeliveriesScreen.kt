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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    courierId: Int,
    vm: DeliveriesViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val available by vm.available.collectAsState()
    val myDeliveries by vm.myDeliveries.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(courierId) {
        vm.loadData(courierId)
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

            // --- СЕКЦІЯ 1: АКТИВНІ ЗАМОВЛЕННЯ ---
            if (myDeliveries.isNotEmpty()) {
                item {
                    Text(
                        "Мої активні замовлення",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(myDeliveries) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Доставка #${item.id}", style = MaterialTheme.typography.titleSmall)
                                Text(getStatusText(item.status), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }

                            Divider(Modifier.padding(vertical = 8.dp))

                            // --- ВІДОБРАЖЕННЯ ІМЕНІ ---
                            Text(
                                "Клієнт: ${item.clientName ?: "Гість"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text("Адреса: ${item.clientAddress}")
                            Text("Телефон: ${item.clientPhone ?: "-"}")

                            Spacer(Modifier.height(12.dp))

                            // --- КНОПКИ ЗМІНИ СТАТУСУ ---
                            when (item.status) {
                                1 -> { // Assigned -> PickedUp
                                    Button(
                                        onClick = { vm.updateStatus(item.id, courierId, 2) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                                    ) {
                                        Text("📦 Я забрав їжу")
                                    }
                                }
                                2 -> { // PickedUp -> Delivered
                                    Button(
                                        onClick = { vm.updateStatus(item.id, courierId, 3) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) {
                                        Text("✅ Замовлення доставлено")
                                    }
                                }
                            }
                        }
                    }
                }
                item { Divider(Modifier.padding(vertical = 16.dp)) }
            }

            // --- СЕКЦІЯ 2: ВІЛЬНІ ЗАМОВЛЕННЯ ---
            item {
                Text("Вільні замовлення", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }

            if (available.isEmpty()) {
                item {
                    Text("Немає вільних замовлень", color = Color.Gray, modifier = Modifier.padding(8.dp))
                }
            }

            items(available) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Замовлення від кухні", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)

                        // --- ВІДОБРАЖЕННЯ ІМЕНІ ---
                        Text(
                            "Клієнт: ${item.clientName ?: "Гість"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

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

        // Відображення помилок (якщо є)
        if (error != null) {
            Snackbar(
                action = { TextButton(onClick = { vm.clearError() }) { Text("OK") } },
                modifier = Modifier.padding(16.dp)
            ) { Text(error!!) }
        }
    }
}

// Допоміжна функція для тексту статусу
fun getStatusText(status: Int): String {
    return when(status) {
        0 -> "Очікує"
        1 -> "Призначено"
        2 -> "В дорозі"
        3 -> "Доставлено"
        else -> "Невідомо"
    }
}