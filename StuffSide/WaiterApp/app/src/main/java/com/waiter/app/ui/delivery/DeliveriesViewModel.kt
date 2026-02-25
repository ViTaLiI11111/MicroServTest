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

    private val _available = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val available = _available.asStateFlow()

    private val _activeDeliveries = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val activeDeliveries = _activeDeliveries.asStateFlow()

    private val _historyDeliveries = MutableStateFlow<List<DeliveryDto>>(emptyList())
    val historyDeliveries = _historyDeliveries.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadData(courierId: Int) {
        viewModelScope.launch {
            try {
                _available.value = deliveryRepo.getAvailable()

                val allMine = deliveryRepo.getMyDeliveries(courierId)

                _activeDeliveries.value = allMine.filter { it.status != 3 }
                _historyDeliveries.value = allMine.filter { it.status == 3 }

            } catch (e: Exception) {
                _error.value = "Failed to load: ${e.message}"
            }
        }
    }

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

    fun payOrder(orderId: String, courierId: Int) {
        viewModelScope.launch {
            try {
                ordersRepo.payOrder(orderId)
                loadData(courierId)
            } catch (e: Exception) {
                _error.value = "Error paying order: ${e.message}"
            }
        }
    }

    fun clearError() { _error.value = null }
}