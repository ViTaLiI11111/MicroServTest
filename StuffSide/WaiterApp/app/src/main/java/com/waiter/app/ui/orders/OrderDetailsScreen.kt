package com.waiter.app.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OrderDetailsScreen(
    vm: OrdersViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val order = vm.selected.collectAsStateWithLifecycle().value

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        order?.let { o ->
            Text("Table: ${o.tableNo}")
            Text("Status: ${o.status}")

            Divider()
            o.items.forEach {
                Text("${it.dishTitle} x${it.qty}")
                Text("@${it.price}")
            }
            Divider()
            Text(
                text = "Total: ${o.total}",
                style = MaterialTheme.typography.titleMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.accept(o.id) }) { Text("Accept") }
                Button(onClick = { vm.complete(o.id) }) { Text("Complete") }
                Button(onClick = onBack) { Text("Back") }
            }
        } ?: Text("No order selected")
    }
}
