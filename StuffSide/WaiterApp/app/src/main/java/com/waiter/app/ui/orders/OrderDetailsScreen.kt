package com.waiter.app.ui.orders

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waiter.app.ui.settings.SettingsViewModel

@Composable
fun OrderDetailsScreen(
    vm: OrdersViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val order = vm.selected.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    val settingsVm: SettingsViewModel = viewModel()
    val myWaiterId by settingsVm.userIdFlow.collectAsState(initial = 0)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (order != null) {
                Text("Замовлення #${order.id.take(4)}...", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Стіл: ${order.tableNo}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        if (order.waiterId != null) {
                            Text("Офіціант ID: ${order.waiterId}", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("⚠️ Офіціант не призначений", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                Text("Страви:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start))
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.dishTitle} x${item.qty}", modifier = Modifier.weight(1f))

                        val (statusText, color) = when(item.itemStatus) {
                            "Ready" -> "Готово" to Color(0xFF2E7D32)
                            "Cooking" -> "Готується" to Color(0xFFFFA000)
                            else -> "Черга" to Color.Gray
                        }
                        Text(statusText, color = color, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(Modifier.padding(vertical = 16.dp))

                Text(
                    text = "Всього: ${order.total} грн",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.weight(1f))


                val isMine = (order.waiterId == myWaiterId)
                val isFree = (order.waiterId == null)

                if (isFree) {
                    Button(
                        onClick = {
                            vm.assignOrder(order.id, myWaiterId) {
                                Toast.makeText(context, "Ви взяли замовлення!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Взяти замовлення в роботу")
                    }
                } else if (isMine) {

                    if (order.isPaid) {
                        Text("✅ ОПЛАЧЕНО", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    } else {
                        Button(
                            onClick = { vm.payOrder(order.id, myWaiterId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("💵 Прийняти оплату") }
                    }

                    Spacer(Modifier.height(8.dp))

                    if (order.status != "completed") {

                        val isKitchenReady = (order.status == "ready")
                        val canComplete = order.isPaid && isKitchenReady

                        Button(
                            onClick = {
                                vm.completeOrder(order.id, myWaiterId, onSuccess = onBack)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = canComplete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if(canComplete) MaterialTheme.colorScheme.secondary else Color.Gray
                            )
                        ) {
                            if (!isKitchenReady) Text("⏳ Чекаємо кухню...")
                            else if (!order.isPaid) Text("💵 Спочатку оплата!")
                            else Text("✅ Завершити обслуговування")
                        }
                    } else {
                        Text("🏁 Замовлення закрито", color = Color.Gray)
                    }
                } else {
                    Text("Це замовлення обслуговує інший офіціант.", color = Color.Red)
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Назад") }

            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}