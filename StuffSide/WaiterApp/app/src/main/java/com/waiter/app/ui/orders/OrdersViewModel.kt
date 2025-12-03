package com.waiter.app.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.data.repo.OrdersRepository
import com.waiter.app.domain.model.UiOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repo: OrdersRepository = OrdersRepository()
) : ViewModel() {

    // Вкладка 1: Вільні
    private val _availableOrders = MutableStateFlow<List<UiOrder>>(emptyList())
    val availableOrders: StateFlow<List<UiOrder>> = _availableOrders

    // Вкладка 2: В роботі (Мої Активні)
    private val _activeOrders = MutableStateFlow<List<UiOrder>>(emptyList())
    val activeOrders: StateFlow<List<UiOrder>> = _activeOrders

    // Вкладка 3: Історія (Мої Завершені) - НОВЕ
    private val _historyOrders = MutableStateFlow<List<UiOrder>>(emptyList())
    val historyOrders: StateFlow<List<UiOrder>> = _historyOrders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selected = MutableStateFlow<UiOrder?>(null)
    val selected: StateFlow<UiOrder?> = _selected

    fun loadData(waiterId: Int) {
        viewModelScope.launch {
            // Показуємо лоадер тільки якщо всі списки пусті
            if (_availableOrders.value.isEmpty() && _activeOrders.value.isEmpty()) {
                _isLoading.value = true
            }
            try {
                // 1. Вільні
                val free = repo.getOrders(type = "DineIn", onlyFree = true)
                _availableOrders.value = free

                // 2. Мої (всі)
                val allMine = repo.getOrders(type = "DineIn", waiterId = waiterId)

                // Розділяємо на Активні та Історію
                _activeOrders.value = allMine.filter { it.status != "completed" }
                _historyOrders.value = allMine.filter { it.status == "completed" }

            } catch (e: Exception) {
                _error.value = "Помилка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun assignOrder(orderId: String, waiterId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.assignOrder(orderId, waiterId)
                loadData(waiterId)
                onSuccess() // Викликаємо callback (наприклад, для Toast)
            } catch (e: Exception) {
                _error.value = "Не вдалося взяти замовлення: ${e.message}"
            }
        }
    }

    fun completeOrder(orderId: String, waiterId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.completeOrder(orderId)
                loadData(waiterId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Не вдалося завершити: ${e.message}"
            }
        }
    }

    fun payOrder(id: String, waiterId: Int) {
        viewModelScope.launch {
            try {
                repo.payOrder(id)
                select(id)
                loadData(waiterId)
            } catch (t: Throwable) {
                _error.value = "Помилка оплати"
            }
        }
    }

    fun select(id: String) {
        viewModelScope.launch {
            try {
                _selected.value = repo.getOrder(id)
            } catch (t: Throwable) {
                _error.value = "Помилка відкриття"
            }
        }
    }

    fun clearError() { _error.value = null }
}