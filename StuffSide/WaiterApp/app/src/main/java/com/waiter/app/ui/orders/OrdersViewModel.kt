package com.waiter.app.ui.orders

import android.util.Log
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

    private val _availableOrders = MutableStateFlow<List<UiOrder>>(emptyList())
    val availableOrders: StateFlow<List<UiOrder>> = _availableOrders

    private val _myOrders = MutableStateFlow<List<UiOrder>>(emptyList())
    val myOrders: StateFlow<List<UiOrder>> = _myOrders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selected = MutableStateFlow<UiOrder?>(null)
    val selected: StateFlow<UiOrder?> = _selected

    // --- ОНОВЛЕНИЙ МЕТОД ЗАВАНТАЖЕННЯ ---
    fun loadData(waiterId: Int) {
        viewModelScope.launch {
            // ХИТРІСТЬ: Вмикаємо спінер, ТІЛЬКИ якщо у нас взагалі немає даних.
            // Якщо це просто оновлення після натискання кнопки - екран не буде блимати.
            val hasData = _availableOrders.value.isNotEmpty() || _myOrders.value.isNotEmpty()
            if (!hasData) {
                _isLoading.value = true
            }

            try {
                // 1. Завантажуємо вільні
                val free = repo.getOrders(type = "DineIn", onlyFree = true)
                _availableOrders.value = free

                // 2. Завантажуємо мої
                val mine = repo.getOrders(type = "DineIn", waiterId = waiterId)
                _myOrders.value = mine

            } catch (e: Exception) {
                _error.value = "Помилка завантаження: ${e.message}"
            } finally {
                // Вимикаємо спінер в будь-якому випадку
                _isLoading.value = false
            }
        }
    }

    fun assignOrder(orderId: String, waiterId: Int) {
        viewModelScope.launch {
            try {
                repo.assignOrder(orderId, waiterId)
                loadData(waiterId)
            } catch (e: Exception) {
                _error.value = "Не вдалося взяти замовлення: ${e.message}"
            }
        }
    }

    fun completeOrder(orderId: String, waiterId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.completeOrder(orderId)
                // Оновлюємо дані БЕЗ блимання (завдяки зміні в loadData)
                loadData(waiterId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Помилка завершення: ${e.message}"
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
                _error.value = "Помилка оплати: ${t.message}"
            }
        }
    }

    fun select(id: String) {
        viewModelScope.launch {
            try {
                val o = repo.getOrder(id)
                _selected.value = o
            } catch (t: Throwable) {
                _error.value = "Не вдалося відкрити деталі"
            }
        }
    }

    fun clearError() { _error.value = null }
}