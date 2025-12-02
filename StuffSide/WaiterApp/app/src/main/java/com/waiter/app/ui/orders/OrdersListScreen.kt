package com.waiter.app.ui.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    vm: OrdersViewModel,
    onOpenDetails: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    // Використовуємо collectAsStateWithLifecycle для кращої роботи з життєвим циклом
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Замовлення") },
                actions = {
                    // Кнопка Оновити
                    IconButton(onClick = { vm.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Оновити"
                        )
                    }
                    // Кнопка Налаштування
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Налаштування"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is OrdersUiState.Loading -> {
                    // Показуємо спіннер по центру
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                is OrdersUiState.Error -> {
                    Text(
                        text = "Помилка: ${s.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is OrdersUiState.ListState -> {
                    if (s.orders.isEmpty()) {
                        Text(
                            text = "Немає активних замовлень",
                            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                        )
                    } else {
                        // Використовуємо LazyColumn для прокручування списку
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.orders) { o ->
                                OrderCard(
                                    order = o,
                                    onClick = { onOpenDetails(o.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: com.waiter.app.domain.model.UiOrder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Верхній рядок: ID та Сума
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id.take(4)}...",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = "${order.total} грн",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ім'я клієнта
            Text(
                text = "👤 ${order.clientName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (order.tableNo > 0) "🍽️ Стіл: ${order.tableNo}" else "🏠 Доставка",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- ДИНАМІЧНА ПЛАШКА СТАТУСУ ---
            val (statusText, bgColor, contentColor) = when (order.status) {
                "new" -> Triple("🆕 Нове (Чекає)", Color(0xFFFFEBEE), Color(0xFFD32F2F)) // Червоний
                "inprogress" -> Triple("👨‍🍳 Готується", Color(0xFFFFF3E0), Color(0xFFE65100)) // Помаранчевий
                "ready" -> Triple("✅ ГОТОВО ДО ВИДАЧІ", Color(0xFFE8F5E9), Color(0xFF2E7D32)) // Зелений
                "completed" -> Triple("🏁 Завершено", Color(0xFFF5F5F5), Color(0xFF757575)) // Сірий
                else -> Triple(order.status, Color.LightGray, Color.Black)
            }

            Surface(
                color = bgColor,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            // --------------------------------
        }
    }
}