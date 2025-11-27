package com.waiter.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.waiter.app.core.UserRole

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вітаємо!", style = MaterialTheme.typography.headlineLarge)
        Text("Оберіть вашу роль для входу", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(48.dp))

        // Кнопка Офіціанта
        Button(
            onClick = { onRoleSelected(UserRole.WAITER) },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text("👨‍🍳 Офіціант", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(16.dp))

        // Кнопка Кур'єра
        Button(
            onClick = { onRoleSelected(UserRole.COURIER) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("🛵 Кур'єр", style = MaterialTheme.typography.titleMedium)
        }
    }
}