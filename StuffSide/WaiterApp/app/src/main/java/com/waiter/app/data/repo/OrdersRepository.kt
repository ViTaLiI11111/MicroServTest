package com.waiter.app.data.repo

import com.waiter.app.data.api.OrdersApi
import com.waiter.app.data.repo.RetrofitModule
import com.waiter.app.domain.mapper.toUi
import com.waiter.app.domain.model.UiOrder

class OrdersRepository(
    private val api: OrdersApi = RetrofitModule.createApi()
) {
    suspend fun getOrders(
        type: String? = null,
        waiterId: Int? = null,
        onlyFree: Boolean? = null
    ): List<UiOrder> =
        api.getOrders(type, waiterId, onlyFree).map { it.toUi() }

    suspend fun payOrder(id: String) {
        val response = api.payOrder(id)
        if (!response.isSuccessful) {
            throw Exception("Failed to pay order: ${response.message()}")
        }
    }

    suspend fun assignOrder(id: String, waiterId: Int) {
        val response = api.assignOrder(id, waiterId)
        if (!response.isSuccessful) {
            throw Exception("Failed to assign order: ${response.message()}")
        }
    }

    suspend fun completeOrder(id: String) {
        val response = api.completeOrder(id)
        if (!response.isSuccessful) {
            throw Exception("Failed to complete order: ${response.message()}")
        }
    }


    suspend fun getOrder(id: String): UiOrder =
        api.getOrder(id).toUi()
}