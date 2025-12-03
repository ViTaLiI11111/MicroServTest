package com.waiter.app.ui.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waiter.app.data.dto.DeliveryDto
import com.waiter.app.data.repo.DeliveryRepository
import com.waiter.app.data.repo.OrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveriesViewModel(
    private val deliveryRepo: DeliveryRepository = DeliveryRepository(),
    private val ordersRepo: OrdersRepository = OrdersRepository()
) : ViewModel() {

    // Вкладка 1: Вільні замовлення (Available)
    private val _available = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val available = _available.asStateFlow()

    // Вкладка 2: Активні доставки (В процесі)
    private val _activeDeliveries = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val activeDeliveries = _activeDeliveries.asStateFlow()

    // Вкладка 3: Історія (Доставлені)
    private val _historyDeliveries = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val historyDeliveries = _historyDeliveries.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadData(courierId: Int) {
        viewModelScope.launch {
            try {
                // 1. Отримуємо вільні замовлення
                _available.value = deliveryRepo.getAvailable()

                // 2. Отримуємо всі замовлення цього кур'єра
                // Бекенд вже сортує їх, але ми розділимо на списки для зручності
                val allMine = deliveryRepo.getMyDeliveries(courierId)

                // 3 = Status Delivered
                _activeDeliveries.value = allMine.filter { it.status != 3 }
                _historyDeliveries.value = allMine.filter { it.status == 3 }

            } catch (e: Exception) {
                _error.value = "Failed to load: ${e.message}"
            }
        }
    }

    // Взяти замовлення в роботу
    fun takeOrder(deliveryId: Int, courierId: Int) {
        viewModelScope.launch {
            try {
                deliveryRepo.takeDelivery(deliveryId, courierId)
                loadData(courierId)
            } catch (e: Exception) {
                _error.value = "Error taking order: ${e.message}"
            }
        }
    }

    // Змінити статус (PickedUp -> Delivered)
    fun updateStatus(deliveryId: Int, courierId: Int, newStatus: Int) {
        viewModelScope.launch {
            try {
                deliveryRepo.updateStatus(deliveryId, courierId, newStatus)
                loadData(courierId)
            } catch (e: Exception) {
                _error.value = "Error updating status: ${e.message}"
            }
        }
    }

    // Прийняти оплату (викликаємо OrderDispatchService через OrdersRepository)
    fun payOrder(orderId: String, courierId: Int) {
        viewModelScope.launch {
            try {
                ordersRepo.payOrder(orderId)
                // Оновлюємо дані, щоб побачити isPaid = true
                loadData(courierId)
            } catch (e: Exception) {
                _error.value = "Error paying order: ${e.message}"
            }
        }
    }

    fun clearError() { _error.value = null }
}