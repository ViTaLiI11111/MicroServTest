package com.waiter.app.data.repo

import com.waiter.app.data.api.OrdersApi
import com.waiter.app.domain.mapper.toUi
import com.waiter.app.domain.model.UiOrder

class OrdersRepository(
    private val api: OrdersApi = RetrofitModule.createApi()
) {
    // Додаємо параметр type.
    suspend fun getOrders(type: String? = null): List<UiOrder> =
        api.getOrders(type).map { it.toUi() }

    suspend fun payOrder(id: String) {
        val response = api.payOrder(id)
        if (!response.isSuccessful) {
            throw Exception("Failed to pay order: ${response.message()}")
        }
    }

    suspend fun getOrder(id: String): UiOrder =
        api.getOrder(id).toUi()
}