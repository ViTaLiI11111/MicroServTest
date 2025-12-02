package com.waiter.app.ui.kitchen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.data.dto.OrderItemDto
import com.waiter.app.data.repo.KitchenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Допоміжний клас для відображення на екрані
data class KitchenUiItem(
    val itemId: Int,
    val orderId: String, // ID замовлення (щоб знати, з якого чеку страва)
    val title: String,
    val qty: Int,
    val status: String, // "Pending", "Cooking", "Ready"
    val orderTime: String? // Можна додати час створення
)

class KitchenViewModel(
    private val repo: KitchenRepository = KitchenRepository()
) : ViewModel() {

    private val _items = MutableStateFlow<List<KitchenUiItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _items.asStateFlow()

    fun loadOrdersForStation(myStationId: Int) {
        viewModelScope.launch {
            try {
                val orders = repo.getOrders()

                val filteredItems = mutableListOf<KitchenUiItem>()

                // Проходимось по всіх замовленнях
                for (order in orders) {
                    // Якщо замовлення вже повністю готове або оплачене - можна не показувати (залежить від логіки)
                    // Але поки показуємо все, що не "Ready"

                    // Шукаємо страви для мого цеху
                    val myItems = order.items.filter { it.stationId == myStationId }

                    for (item in myItems) {
                        // Не показуємо те, що вже видано (Ready), щоб не засмічувати екран
                        // Або показуємо, але в кінці списку.
                        // Давайте поки показувати все, крім виданих, або сортувати.

                        filteredItems.add(
                            KitchenUiItem(
                                itemId = item.id,
                                orderId = order.id.take(4), // Короткий ID
                                title = item.dishTitle ?: "Unknown",
                                qty = item.qty,
                                status = item.status,
                                orderTime = null // Тут можна розпарсити created_at
                            )
                        )
                    }
                }

                // Сортуємо: спочатку ті, що готуються, потім нові, потім готові
                _items.value = filteredItems.sortedBy {
                    when(it.status) {
                        "Cooking" -> 0
                        "Pending" -> 1
                        else -> 2
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun advanceStatus(itemId: Int, currentStatus: String, myStationId: Int) {
        viewModelScope.launch {
            // Логіка переходу статусів: Pending -> Cooking -> Ready
            val newStatusInt = when (currentStatus) {
                "Pending" -> 1 // Cooking
                "Cooking" -> 2 // Ready
                else -> return@launch
            }

            try {
                repo.updateItemStatus(itemId, newStatusInt)
                loadOrdersForStation(myStationId) // Оновлюємо список
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}