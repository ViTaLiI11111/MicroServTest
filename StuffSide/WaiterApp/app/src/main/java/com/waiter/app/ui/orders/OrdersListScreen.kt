package com.waiter.app.ui.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.waiter.app.domain.model.UiOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(
    vm: OrdersViewModel,
    onOpenDetails: (String) -> Unit,
    onOpenSettings: () -> Unit // <-- Новий параметр
) {
    val state by vm.state.collectAsState()

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
                    // Нова кнопка Налаштування
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
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            when (val s = state) {
                is OrdersUiState.Loading -> Text("Завантаження…")
                is OrdersUiState.Error   -> Text(s.message)
                is OrdersUiState.ListState    -> {
                    s.orders.forEach { o ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { onOpenDetails(o.id) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Order #${o.id}")
                                Text("Table: ${o.tableNo}")
                                Text("Status: ${o.status}")
                                Text("Total: ${o.total}")
                            }
                        }
                    }
                }
            }
        }
    }
}

