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
    val status: String, // "Pending", "Cooking", "Ready"
    val sortIndex: Int
)

class KitchenViewModel(
    private val repo: KitchenRepository = KitchenRepository()
) : ViewModel() {

    // Вкладка 1: Черга
    private val _pendingItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val pendingItems = _pendingItems.asStateFlow()

    // Вкладка 2: В роботі
    private val _cookingItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val cookingItems = _cookingItems.asStateFlow()

    // Вкладка 3: Видано (Готові) - НОВЕ
    private val _readyItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val readyItems = _readyItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadOrdersForStation(myStationId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val orders = repo.getOrders()

                val pendingList = mutableListOf<KitchenUiItem>()
                val cookingList = mutableListOf<KitchenUiItem>()
                val readyList = mutableListOf<KitchenUiItem>()

                for (order in orders) {
                    // Ігноруємо ТІЛЬКИ повністю закриті (архівні) замовлення.
                    // Замовлення зі статусом "ready" ми залишаємо, щоб бачити історію видачі.
                    if (order.status == "completed") continue

                    val myItems = order.items.filter { it.stationId == myStationId }

                    for (item in myItems) {
                        val uiItem = KitchenUiItem(
                            itemId = item.id,
                            orderId = order.id.take(4),
                            title = item.dishTitle ?: "Unknown",
                            qty = item.qty,
                            status = item.status ?: "Pending",
                            sortIndex = 0
                        )

                        // Розподіляємо по 3-х списках
                        when (item.status) {
                            "Cooking" -> cookingList.add(uiItem)
                            "Ready" -> readyList.add(uiItem) // Додаємо в готове
                            else -> pendingList.add(uiItem) // Pending
                        }
                    }
                }

                // Оновлюємо StateFlow
                _pendingItems.value = pendingList.distinctBy { it.itemId }
                _cookingItems.value = cookingList.distinctBy { it.itemId }

                // Сортуємо готові так, щоб останні зроблені були зверху (за ID)
                _readyItems.value = readyList
                    .distinctBy { it.itemId }
                    .sortedByDescending { it.itemId }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun advanceStatus(itemId: Int, currentStatus: String, myStationId: Int) {
        viewModelScope.launch {
            // Логіка переходу
            val newStatusInt = when (currentStatus) {
                "Pending" -> 1 // -> Cooking
                "Cooking" -> 2 // -> Ready
                else -> return@launch // З Ready далі нікуди
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