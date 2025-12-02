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
    data class ListState(val orders: List<UiOrder>) : OrdersUiState
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

                // --- ВАЖЛИВА ЗМІНА ---
                // Запитуємо тільки "DineIn" (в закладі).
                // Доставки більше не будуть показуватись офіціанту.
                val items = repo.getOrders(type = "DineIn")

                _state.value = OrdersUiState.ListState(items)
            } catch (t: Throwable) {
                _state.value = OrdersUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    fun select(id: String) {
        viewModelScope.launch {
            try {
                Log.d("OrdersViewModel", "Requesting details for ID: $id")
                val o = repo.getOrder(id)
                _selected.value = o
            } catch (t: Throwable) {
                Log.e("OrdersViewModel", "Error loading details", t)
                t.printStackTrace()
                _selected.value = null
            }
        }
    }

    fun payOrder(id: String) {
        viewModelScope.launch {
            try {
                repo.payOrder(id)
                select(id)
                refresh()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun accept(id: String) {}
    fun complete(id: String) {}
}