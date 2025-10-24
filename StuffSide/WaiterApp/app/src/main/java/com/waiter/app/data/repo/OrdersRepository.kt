package com.waiter.app.data.repo

import com.waiter.app.data.repo.api.OrdersApi
import com.waiter.app.domain.mapper.toUi
import com.waiter.app.domain.model.UiOrder

class OrdersRepository(
    private val api: OrdersApi = RetrofitModule.createApi()
) {
    suspend fun getOrders(): List<UiOrder> =
        api.getOrders().map { it.toUi() }

    suspend fun getOrder(id: String): UiOrder =
        api.getOrder(id).toUi()
}
