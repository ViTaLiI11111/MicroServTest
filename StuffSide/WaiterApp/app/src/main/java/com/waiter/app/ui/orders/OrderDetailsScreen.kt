package com.waiter.app.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (order != null) {
            // Відображення даних (успішне завантаження)
            Text("Замовлення #${order.id.take(4)}...", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            Text("Клієнт: ${order.clientName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (order.tableNo > 0) {
                Text("Стіл: ${order.tableNo}", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("Тип: Доставка", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }

            Text("Статус: ${order.status}", style = MaterialTheme.typography.bodyLarge)

            Divider(Modifier.padding(vertical = 16.dp))

            Text("Склад замовлення:", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            order.items.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${it.dishTitle} x${it.qty}")
                    Text("${it.price * it.qty} грн")
                }
            }

            Divider(Modifier.padding(vertical = 16.dp))

            Text(
                text = "Всього: ${order.total} грн",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.status == "new") {
                    Button(onClick = { vm.accept(order.id) }) { Text("Прийняти") }
                }
                Button(onClick = { vm.complete(order.id) }) { Text("Завершити") }
                Button(onClick = onBack) { Text("Назад") }
            }
        } else {
            // Стан завантаження або помилки
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Завантаження даних...", style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Назад") }
        }
    }
}