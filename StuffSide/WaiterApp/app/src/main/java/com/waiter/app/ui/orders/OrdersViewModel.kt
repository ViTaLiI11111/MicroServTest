package com.waiter.app.ui.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.waiter.app.domain.model.UiOrder
import com.waiter.app.data.repo.OrdersRepository


sealed interface OrdersUiState {
    data object Loading : OrdersUiState
    data class ListState(val orders: kotlin.collections.List<UiOrder>) : OrdersUiState
    data class Error(val message: String) : OrdersUiState
}

class OrdersViewModel(
    private val repo: OrdersRepository = OrdersRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val state: StateFlow<OrdersUiState> = _state

    private val _selected = MutableStateFlow<UiOrder?>(null)
    val selected: StateFlow<UiOrder?> = _selected

    init {
        refresh()
    }


    fun refresh() {
        viewModelScope.launch {
            try {
                _state.value = OrdersUiState.Loading
                val items = repo.getOrders()
                _state.value = OrdersUiState.ListState(items)
            } catch (t: Throwable) {
                _state.value = OrdersUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    fun select(id: String) {
        viewModelScope.launch {
            try {
                // Додамо лог, щоб бачити, який ID ми запитуємо
                Log.d("OrdersViewModel", "Requesting details for ID: $id")

                val o = repo.getOrder(id)
                _selected.value = o

                Log.d("OrdersViewModel", "Details loaded successfully")
            } catch (t: Throwable) {
                // --- ОСЬ ТУТ ВАЖЛИВА ЗМІНА ---
                Log.e("OrdersViewModel", "Error loading details", t)
                t.printStackTrace() // Друкуємо помилку в консоль
                // -----------------------------

                _selected.value = null
            }
        }
    }

    fun accept(id: String) {
        // TODO: виклик repo для Accept і оновлення стану
    }

    fun complete(id: String) {
        // TODO: виклик repo для Complete і оновлення стану
    }
}
