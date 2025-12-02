package com.waiter.app.ui.kitchen

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(
    stationId: Int,
    vm: KitchenViewModel = viewModel(),
    onOpenSettings: () -> Unit // <--- Перехід на екран налаштувань
) {
    val pendingItems by vm.pendingItems.collectAsState()
    val cookingItems by vm.cookingItems.collectAsState()

    // Стан вкладок: 0 = Черга (Pending), 1 = Готується (Cooking)
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(stationId) {
        vm.loadOrdersForStation(stationId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Цех #$stationId") },
                    actions = {
                        // Оновити
                        IconButton(onClick = { vm.loadOrdersForStation(stationId) }) {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                        // Налаштування (замість Exit)
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }
                )
                // Вкладки з лічильниками
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Черга (${pendingItems.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Готується (${cookingItems.size})") }
                    )
                }
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Вибір списку залежно від вкладки
            val listToShow = if (selectedTab == 0) pendingItems else cookingItems

            if (listToShow.isEmpty()) {
                item {
                    Text(
                        text = if(selectedTab == 0) "Немає нових замовлень" else "Нічого не готується",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }

            // ВАЖЛИВО: key = { it.itemId } запобігає глюкам скролінгу і дублюванню
            items(items = listToShow, key = { it.itemId }) { item ->
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
    // Колір залежить від статусу: Черга -> Звичайний, В роботі -> Помаранчевий
    val cardColor = if (item.status == "Cooking") Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
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

            Button(
                onClick = onAdvance,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    // Синій для "Почати", Зелений для "Готово"
                    containerColor = if (item.status == "Pending") MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)
                )
            ) {
                val btnText = if (item.status == "Pending") "🔥 Почати готувати" else "✅ ГОТОВО!"
                Text(btnText)
            }
        }
    }
}