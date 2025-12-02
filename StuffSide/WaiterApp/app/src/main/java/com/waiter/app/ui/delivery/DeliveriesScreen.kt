package com.waiter.app.ui.delivery

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
import com.waiter.app.data.dto.DeliveryDto

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

    // Завантаження даних при старті
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

            // ============================================================
            // СЕКЦІЯ 1 (ТЕПЕР ЗВЕРХУ): ВІЛЬНІ ЗАМОВЛЕННЯ
            // ============================================================
            item {
                Text(
                    "Вільні замовлення",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
            }

            if (available.isEmpty()) {
                item {
                    Text(
                        "Немає вільних замовлень",
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            items(available) { item ->
                AvailableDeliveryCard(item, courierId, vm)
            }

            // Розділювач між секціями
            item { Divider(Modifier.padding(vertical = 24.dp)) }


            // ============================================================
            // СЕКЦІЯ 2 (ТЕПЕР ЗНИЗУ): МОЇ ЗАМОВЛЕННЯ (Активні та історія)
            // ============================================================
            if (myDeliveries.isNotEmpty()) {
                item {
                    Text(
                        "Мої замовлення (Історія)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(myDeliveries) { item ->
                    DeliveryCard(item, courierId, vm)
                }
            }
        }

        // Відображення помилок
        if (error != null) {
            Snackbar(
                action = { TextButton(onClick = { vm.clearError() }) { Text("OK") } },
                modifier = Modifier.padding(16.dp)
            ) { Text(error!!) }
        }
    }
}

// --- КАРТКА МОГО ЗАМОВЛЕННЯ ---
@Composable
fun DeliveryCard(item: DeliveryDto, courierId: Int, vm: DeliveriesViewModel) {
    // Визначаємо колір картки: якщо доставлено (status 3) - сірий, інакше - світло-зелений
    val cardColor = if (item.status == 3) Color(0xFFF5F5F5) else Color(0xFFE8F5E9)
    val elevation = if (item.status == 3) 1.dp else 4.dp

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Верхній рядок: ID та Статус
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Доставка #${item.id}", style = MaterialTheme.typography.titleSmall)
                Text(getStatusText(item.status), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Divider(Modifier.padding(vertical = 8.dp))

            // Дані клієнта
            Text(
                "Клієнт: ${item.clientName ?: "Гість"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Адреса: ${item.clientAddress}")
            Text("Телефон: ${item.clientPhone ?: "-"}")

            Spacer(Modifier.height(8.dp))

            // --- БЛОК ОПЛАТИ (Сума і Статус) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Сума: ${item.total} грн",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                if (item.isPaid) {
                    Text("✅ ОПЛАЧЕНО", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                } else {
                    Text("💵 НЕ ОПЛАЧЕНО", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            // --- КНОПКА "ПРИЙНЯТИ ОПЛАТУ" ---
            // Показуємо, тільки якщо ще не оплачено І замовлення ще не закрите (не Delivered)
            if (!item.isPaid && item.status != 3) {
                Button(
                    onClick = { vm.payOrder(item.orderId, courierId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("💰 Прийняти оплату готівкою")
                }
                Spacer(Modifier.height(8.dp))
            }

            // --- УПРАВЛІННЯ СТАТУСОМ ДОСТАВКИ ---
            when (item.status) {
                1 -> { // Assigned (Призначено) -> PickedUp (Забрав)
                    val isReady = item.isReadyForPickup
                    Button(
                        onClick = { vm.updateStatus(item.id, courierId, 2) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isReady, // Активна тільки якщо кухня сказала "Ready"
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReady) Color(0xFF2196F3) else Color.Gray
                        )
                    ) {
                        Text(if (isReady) "📦 Я забрав їжу" else "⏳ Кухня ще готує...")
                    }
                }
                2 -> { // PickedUp (В дорозі) -> Delivered (Доставлено)
                    // БЛОКУВАННЯ: Не можна завершити, якщо не оплачено!
                    val canDeliver = item.isPaid

                    Button(
                        onClick = { vm.updateStatus(item.id, courierId, 3) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canDeliver,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canDeliver) Color(0xFF4CAF50) else Color.Gray
                        )
                    ) {
                        if (canDeliver) {
                            Text("✅ Замовлення доставлено")
                        } else {
                            Text("⚠️ Спочатку прийміть оплату!")
                        }
                    }
                }
                3 -> { // Delivered (Вже доставлено)
                    // Кнопок немає, просто інформація
                    Text(
                        text = "🏁 Замовлення закрито",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
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

            Text(
                "Клієнт: ${item.clientName ?: "Гість"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Куди: ${item.clientAddress}")
            Text("Сума: ${item.total} грн", fontWeight = FontWeight.Bold)

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

fun getStatusText(status: Int): String {
    return when(status) {
        0 -> "Очікує"
        1 -> "Призначено"
        2 -> "В дорозі"
        3 -> "Доставлено"
        else -> "Невідомо"
    }
}