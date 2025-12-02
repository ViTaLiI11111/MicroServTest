package com.waiter.app.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OrderDetailsScreen(
    vm: OrdersViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val order = vm.selected.collectAsStateWithLifecycle().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (order != null) {
            // Заголовок
            Text("Замовлення #${order.id.take(4)}...", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Клієнт
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Клієнт: ${order.clientName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(if (order.tableNo > 0) "🍽️ Стіл: ${order.tableNo}" else "🏠 Доставка")
                }
            }
            Spacer(Modifier.height(16.dp))

            // --- ГОЛОВНИЙ СТАТУС ЗАМОВЛЕННЯ ---
            // Цей статус ("ready") ставить БЕКЕНД автоматично, коли кухарі закінчили всі страви
            val (statusText, statusColor) = when (order.status) {
                "new" -> "🆕 Нове (Чекає підтвердження)" to Color.Red
                "inprogress" -> "👨‍🍳 Кухня готує..." to Color(0xFFFFA000) // Помаранчевий
                "ready" -> "✅ ГОТОВО ДО ВИДАЧІ" to Color(0xFF2E7D32) // Зелений
                "completed" -> "🏁 Завершено" to Color.Gray
                else -> order.status to Color.Black
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(Modifier.padding(vertical = 16.dp))

            // --- СПИСОК СТРАВ ЗІ СТАТУСАМИ ---
            Text("Готовність страв:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(8.dp))

            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Назва і кількість
                    Text("${item.dishTitle} x${item.qty}", modifier = Modifier.weight(1f))

                    // Статус конкретної страви
                    val (itemStatusText, itemColor) = when(item.itemStatus) {
                        "Ready" -> "Готово" to Color(0xFF2E7D32)
                        "Cooking" -> "Готується" to Color(0xFFFFA000)
                        else -> "Черга" to Color.Gray
                    }

                    Text(
                        text = itemStatusText,
                        color = itemColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Divider(Modifier.padding(vertical = 16.dp))

            // Всього
            Text(
                text = "Всього: ${order.total} грн",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // --- Блок оплати ---
            Spacer(Modifier.height(16.dp))
            if (order.isPaid) {
                Text("✅ ОПЛАЧЕНО", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            } else {
                Button(
                    onClick = { vm.payOrder(order.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("💵 Прийняти оплату") }
            }

            Spacer(Modifier.weight(1f))

            // --- КНОПКИ ДІЙ ОФІЦІАНТА ---
            // Офіціант натискає кнопку ТІЛЬКИ на початку ("Прийняти")
            // і в самому кінці ("Завершити").
            // "Готово" з'являється САМО, коли кухарі все зроблять.

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.status == "new") {
                    Button(
                        onClick = { vm.accept(order.id) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Прийняти в роботу") }
                }

                if (order.status != "completed") {
                    Button(
                        onClick = { vm.complete(order.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("Завершити") }
                }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Назад") }

        } else {
            CircularProgressIndicator()
        }
    }
}