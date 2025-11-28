package com.waiter.app.ui.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.data.dto.DeliveryDto
import com.waiter.app.data.repo.DeliveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveriesViewModel(
    private val repo: DeliveryRepository = DeliveryRepository()
) : ViewModel() {

    // Список доступних замовлень
    private val _available = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val available = _available.asStateFlow()

    // Список моїх замовлень (активних)
    private val _myDeliveries = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val myDeliveries = _myDeliveries.asStateFlow()

    // Стан помилки (для Toast/Snackbar)
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadData(courierId: Int) {
        viewModelScope.launch {
            try {
                // Завантажуємо паралельно
                _available.value = repo.getAvailable()
                _myDeliveries.value = repo.getMyDeliveries(courierId)
            } catch (e: Exception) {
                _error.value = "Failed to load: ${e.message}"
            }
        }
    }

    fun takeOrder(deliveryId: Int, courierId: Int) {
        viewModelScope.launch {
            try {
                repo.takeDelivery(deliveryId, courierId)
                // Після успіху оновлюємо списки
                loadData(courierId)
            } catch (e: Exception) {
                _error.value = "Error taking order: ${e.message}"
            }
        }
    }

    fun clearError() { _error.value = null }

    fun updateStatus(deliveryId: Int, courierId: Int, newStatus: Int) {
        viewModelScope.launch {
            try {
                repo.updateStatus(deliveryId, courierId, newStatus)
                // Одразу оновлюємо список, щоб кнопка змінилася або замовлення зникло (якщо Delivered)
                loadData(courierId)
            } catch (e: Exception) {
                _error.value = "Error updating status: ${e.message}"
            }
        }
    }
}