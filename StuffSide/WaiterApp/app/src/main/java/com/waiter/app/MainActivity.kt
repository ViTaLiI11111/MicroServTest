package com.waiter.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// імпортуємо новий граф
import com.waiter.app.ui.navigation.RootNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // AppNav() // Старий виклик
            RootNavGraph() // Новий виклик
        }
    }
}