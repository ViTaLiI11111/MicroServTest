package com.waiter.app.ui.orders

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
                val o = repo.getOrder(id)
                _selected.value = o
            } catch (t: Throwable) {
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
