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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waiter.app.domain.model.UiOrder
import com.waiter.app.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    vm: OrdersViewModel,
    settingsVm: SettingsViewModel = viewModel(), // Отримуємо ID офіціанта з налаштувань
    onOpenDetails: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    // Отримуємо поточний ID офіціанта
    val waiterId by settingsVm.userIdFlow.collectAsState(initial = 0)

    val available by vm.availableOrders.collectAsState()
    val myOrders by vm.myOrders.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    // Стан вкладок: 0 = Вільні, 1 = Мої
    var selectedTab by remember { mutableIntStateOf(0) }

    // Завантажуємо дані при вході
    LaunchedEffect(waiterId) {
        if (waiterId != 0) vm.loadData(waiterId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Зал (Офіціант)") },
                    actions = {
                        IconButton(onClick = { vm.loadData(waiterId) }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }
                )

                // --- ВКЛАДКИ ---
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Вільні столики") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Мої столики") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Вибираємо список залежно від вкладки
                    val listToShow = if (selectedTab == 0) available else myOrders

                    if (listToShow.isEmpty()) {
                        item {
                            Text(
                                text = if (selectedTab == 0) "Немає вільних столиків" else "У вас немає активних замовлень",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    }

                    items(listToShow) { o ->
                        OrderCard(
                            order = o,
                            isMyOrder = (selectedTab == 1),
                            onTake = { vm.assignOrder(o.id, waiterId) }, // Кнопка "Взяти"
                            onClick = { onOpenDetails(o.id) }            // Відкрити деталі
                        )
                    }
                }
            }

            if (error != null) {
                Snackbar(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter).padding(16.dp),
                    action = { TextButton(onClick = { vm.clearError() }) { Text("OK") } }
                ) { Text(error!!) }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: UiOrder,
    isMyOrder: Boolean,
    onTake: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Верхній рядок
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Стіл №${order.tableNo}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${order.total} грн", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(8.dp))
            Text("Статус: ${order.status}")

            if (order.isPaid) {
                Text("✅ Оплачено", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            } else {
                Text("💵 Не оплачено", color = Color.Red)
            }

            Spacer(Modifier.height(12.dp))

            // Якщо це "Вільний столик", показуємо кнопку "Взяти"
            if (!isMyOrder) {
                Button(
                    onClick = onTake,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("🙋‍♂️ Обслуговувати цей стіл")
                }
            } else {
                // Якщо "Мій столик"
                Text(
                    "Натисніть, щоб керувати",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}