package com.waiter.app.ui.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waiter.app.data.dto.DeliveryDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveriesScreen(
    courierId: Int,
    vm: DeliveriesViewModel = viewModel(),
    onOpenSettings: () -> Unit
) {
    val available by vm.available.collectAsState()
    val active by vm.activeDeliveries.collectAsState()
    val history by vm.historyDeliveries.collectAsState()
    val error by vm.error.collectAsState()

    // 0 = Вільні, 1 = Активні, 2 = Історія
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(courierId) {
        vm.loadData(courierId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Кабінет Кур'єра") },
                    actions = {
                        IconButton(onClick = { vm.loadData(courierId) }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }
                )
                // --- 3 ВКЛАДКИ ---
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Вільні") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Активні") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Історія") }
                    )
                }
            }
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).padding(16.dp)) {

            // Вибираємо список для відображення
            val listToShow = when(selectedTab) {
                0 -> available
                1 -> active
                else -> history
            }

            if (listToShow.isEmpty()) {
                item {
                    Text(
                        "Список порожній",
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                items(listToShow) { item ->
                    if (selectedTab == 0) {
                        AvailableDeliveryCard(item, courierId, vm)
                    } else {
                        // Для Активних та Історії використовуємо одну картку
                        DeliveryCard(item, courierId, vm)
                    }
                }
            }
        }

        if (error != null) {
            Snackbar(
                action = { TextButton(onClick = { vm.clearError() }) { Text("OK") } },
                modifier = Modifier.padding(16.dp)
            ) { Text(error!!) }
        }
    }
}

// --- КАРТКА АКТИВНОГО / ЗАВЕРШЕНОГО ЗАМОВЛЕННЯ ---
@Composable
fun DeliveryCard(item: DeliveryDto, courierId: Int, vm: DeliveriesViewModel) {
    val isHistory = (item.status == 3)
    // Якщо історія - картка сіра, якщо активне - світло-зелена
    val cardColor = if (isHistory) Color(0xFFF5F5F5) else Color(0xFFE8F5E9)
    val elevation = if (isHistory) 1.dp else 4.dp

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Доставка #${item.id}", style = MaterialTheme.typography.titleSmall)
                Text(getStatusText(item.status), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Divider(Modifier.padding(vertical = 8.dp))

            // Info
            Text("Клієнт: ${item.clientName ?: "Гість"}", fontWeight = FontWeight.Bold)
            Text("Адреса: ${item.clientAddress}")
            Text("Телефон: ${item.clientPhone ?: "-"}")
            Spacer(Modifier.height(8.dp))

            // Payment Info
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("${item.total} грн", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                if (item.isPaid) {
                    Text("✅ ОПЛАЧЕНО", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                } else {
                    Text("💵 НЕ ОПЛАЧЕНО", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
            Divider(Modifier.padding(vertical = 8.dp))

            // --- ДІЇ ---
            if (isHistory) {
                Text(
                    "🏁 Доставлено",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Кнопка оплати (якщо не оплачено)
                if (!item.isPaid) {
                    Button(
                        onClick = { vm.payOrder(item.orderId, courierId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("💰 Прийняти оплату") }
                    Spacer(Modifier.height(8.dp))
                }

                // Кнопки статусу
                when (item.status) {
                    1 -> { // Assigned -> PickedUp
                        val isReady = item.isReadyForPickup
                        Button(
                            onClick = { vm.updateStatus(item.id, courierId, 2) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isReady,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReady) Color(0xFF2196F3) else Color.Gray
                            )
                        ) { Text(if (isReady) "📦 Я забрав їжу" else "⏳ Кухня ще готує...") }
                    }
                    2 -> { // PickedUp -> Delivered
                        val canDeliver = item.isPaid
                        Button(
                            onClick = { vm.updateStatus(item.id, courierId, 3) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = canDeliver,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canDeliver) Color(0xFF4CAF50) else Color.Gray
                            )
                        ) {
                            if (canDeliver) Text("✅ Замовлення доставлено")
                            else Text("⚠️ Спочатку прийміть оплату!")
                        }
                    }
                }
            }
        }
    }
}

// --- КАРТКА ВІЛЬНОГО ЗАМОВЛЕННЯ ---
@Composable
fun AvailableDeliveryCard(item: DeliveryDto, courierId: Int, vm: DeliveriesViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Замовлення від кухні", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            Text("Клієнт: ${item.clientName ?: "Гість"}", fontWeight = FontWeight.Bold)
            Text("Куди: ${item.clientAddress}")
            Text("Сума: ${item.total} грн", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.takeOrder(item.id, courierId) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Взяти в роботу") }
        }
    }
}

fun getStatusText(status: Int): String {
    return when(status) {
        0 -> "Очікує"
        1 -> "Призначено"
        2 -> "В дорозі"
        3 -> "Доставлено"
        else -> "Невідомо"
    }
}