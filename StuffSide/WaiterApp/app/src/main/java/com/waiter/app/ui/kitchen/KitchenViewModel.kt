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
    val status: String,
    val sortIndex: Int
)

class KitchenViewModel(
    private val repo: KitchenRepository = KitchenRepository()
) : ViewModel() {

    private val _pendingItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val pendingItems = _pendingItems.asStateFlow()

    private val _cookingItems = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val cookingItems = _cookingItems.asStateFlow()

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

                        when (item.status) {
                            "Cooking" -> cookingList.add(uiItem)
                            "Ready" -> readyList.add(uiItem)
                            else -> pendingList.add(uiItem)
                        }
                    }
                }

                _pendingItems.value = pendingList.distinctBy { it.itemId }
                _cookingItems.value = cookingList.distinctBy { it.itemId }

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
                "Pending" -> 1
                "Cooking" -> 2
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