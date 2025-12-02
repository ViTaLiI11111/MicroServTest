package com.waiter.app.ui.kitchen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.data.repo.KitchenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KitchenUiItem(
    val itemId: Int,
    val orderId: String,
    val title: String,
    val qty: Int,
    val status: String, // "Pending", "Cooking"
    val sortIndex: Int
)

class KitchenViewModel(
    private val repo: KitchenRepository = KitchenRepository()
) : ViewModel() {

    // Вкладка 1: Черга (Pending)
    private val _pendingItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val pendingItems = _pendingItems.asStateFlow()

    // Вкладка 2: В роботі (Cooking)
    private val _cookingItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val cookingItems = _cookingItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadOrdersForStation(myStationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val orders = repo.getOrders()

                val pendingList = mutableListOf<KitchenUiItem>()
                val cookingList = mutableListOf<KitchenUiItem>()

                for (order in orders) {
                    // Ігноруємо закриті або повністю готові замовлення
                    if (order.status == "completed" || order.status == "ready") continue

                    val myItems = order.items.filter { it.stationId == myStationId }

                    for (item in myItems) {
                        // Ігноруємо вже видані страви
                        if (item.status == "Ready") continue

                        val uiItem = KitchenUiItem(
                            itemId = item.id,
                            orderId = order.id.take(4),
                            title = item.dishTitle ?: "Unknown",
                            qty = item.qty,
                            status = item.status ?: "Pending",
                            sortIndex = 0
                        )

                        // Розподіляємо по списках
                        if (item.status == "Cooking") {
                            cookingList.add(uiItem)
                        } else {
                            // Pending або null
                            pendingList.add(uiItem)
                        }
                    }
                }

                _pendingItems.value = pendingList.distinctBy { it.itemId }
                _cookingItems.value = cookingList.distinctBy { it.itemId }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun advanceStatus(itemId: Int, currentStatus: String, myStationId: Int) {
        viewModelScope.launch {
            val newStatusInt = when (currentStatus) {
                "Pending" -> 1 // Перехід в Cooking
                "Cooking" -> 2 // Перехід в Ready (зникне з екрану)
                else -> return@launch
            }

            try {
                repo.updateItemStatus(itemId, newStatusInt)
                loadOrdersForStation(myStationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}