package com.waiter.app.ui.orders

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waiter.app.domain.model.UiOrder
import com.waiter.app.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    vm: OrdersViewModel,
    settingsVm: SettingsViewModel = viewModel(), // Потрібен для отримання ID офіціанта
    onOpenDetails: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current // Контекст для Toast
    val waiterId by settingsVm.userIdFlow.collectAsState(initial = 0)

    // Підписуємося на три різні списки з ViewModel
    val available by vm.availableOrders.collectAsState()
    val active by vm.activeOrders.collectAsState()
    val history by vm.historyOrders.collectAsState()

    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    // Стан вкладок: 0=Вільні, 1=В роботі, 2=Історія
    var selectedTab by remember { mutableIntStateOf(0) }

    // Завантажуємо дані при вході на екран
    LaunchedEffect(waiterId) {
        if (waiterId != 0) vm.loadData(waiterId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Зал (Офіціант)") },
                    actions = {
                        // Кнопка оновлення
                        IconButton(onClick = { vm.loadData(waiterId) }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                        // Кнопка налаштувань
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
                        text = { Text("Вільні") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("В роботі") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Історія") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Визначаємо, який список показувати
                    val listToShow = when(selectedTab) {
                        0 -> available
                        1 -> active
                        else -> history
                    }

                    if (listToShow.isEmpty()) {
                        item {
                            Text(
                                text = "Список порожній",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    }

                    items(listToShow) { o ->
                        OrderCard(
                            order = o,
                            tabIndex = selectedTab, // Передаємо індекс вкладки для логіки відображення
                            onTake = {
                                // Викликаємо метод ViewModel і показуємо Toast при успіху
                                vm.assignOrder(o.id, waiterId) {
                                    Toast.makeText(context, "Ви взяли столик №${o.tableNo}!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onClick = { onOpenDetails(o.id) }
                        )
                    }
                }
            }

            // Відображення помилки (якщо є)
            if (error != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = { TextButton(onClick = { vm.clearError() }) { Text("OK") } }
                ) { Text(error!!) }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: UiOrder,
    tabIndex: Int, // 0=Free, 1=Active, 2=History
    onTake: () -> Unit,
    onClick: () -> Unit
) {
    // Якщо це "Історія", робимо картку сірою, щоб відрізнялася
    val cardColor = if (tabIndex == 2) Color(0xFFF5F5F5) else Color.White
    val elevation = if (tabIndex == 2) 1.dp else 3.dp

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Верхній рядок: Номер столу та Сума
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Стіл №${order.tableNo}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${order.total} грн",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            // Статус
            val statusText = if (tabIndex == 2) "🏁 Завершено" else "Статус: ${order.status}"
            Text(statusText, color = if(tabIndex == 2) Color.Gray else Color.Black)

            // Оплата
            if (order.isPaid) {
                Text("✅ Оплачено", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            } else {
                Text("💵 Не оплачено", color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            // --- КНОПКА ДІЇ ---
            if (tabIndex == 0) {
                // Вкладка "Вільні": Кнопка "Взяти"
                Button(
                    onClick = onTake,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("🙋‍♂️ Взяти замовлення")
                }
            } else if (tabIndex == 1) {
                // Вкладка "В роботі": Підказка
                Text(
                    "Натисніть на картку для управління",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            // Для "Історії" (tabIndex == 2) нічого не показуємо знизу
        }
    }
}