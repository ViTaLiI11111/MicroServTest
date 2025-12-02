package com.waiter.app.ui.kitchen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(
    stationId: Int, // Отримуємо з Settings
    vm: KitchenViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val items by vm.items.collectAsState()

    // Авто-завантаження при старті
    LaunchedEffect(stationId) {
        vm.loadOrdersForStation(stationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Цех #$stationId") },
                actions = {
                    IconButton(onClick = { vm.loadOrdersForStation(stationId) }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Exit")
                    }
                }
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (items.isEmpty()) {
                item { Text("Замовлень немає", modifier = Modifier.padding(16.dp)) }
            }

            items(items) { item ->
                KitchenItemCard(
                    item = item,
                    onAdvance = { vm.advanceStatus(item.itemId, item.status, stationId) }
                )
            }
        }
    }
}

@Composable
fun KitchenItemCard(item: KitchenUiItem, onAdvance: () -> Unit) {
    // Вибираємо колір залежно від статусу
    val (cardColor, textColor) = when (item.status) {
        "Cooking" -> Color(0xFFFFF3E0) to Color(0xFFE65100) // Помаранчевий (Готується)
        "Ready" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)   // Зелений (Готово)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface // Сірий (Чекає)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Шапка: Назва та кількість
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "x${item.qty}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text("Замовлення #${item.orderId}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))

            // Кнопка зміни статусу
            if (item.status != "Ready") {
                Button(
                    onClick = onAdvance,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.status == "Pending") MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)
                    )
                ) {
                    val btnText = if (item.status == "Pending") "Почати готувати" else "ГОТОВО!"
                    Text(btnText)
                }
            } else {
                Text(
                    "✅ Видано",
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}