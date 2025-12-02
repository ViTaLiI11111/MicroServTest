package com.waiter.app.data.repo

import com.waiter.app.data.dto.OrderDto
import com.waiter.app.data.dto.UpdateItemStatusRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KitchenRepository {
    private val api = RetrofitModule.createApi()

    // Кухар має бачити ВСІ замовлення (і зал, і доставку), тому type = null
    suspend fun getOrders(): List<OrderDto> = withContext(Dispatchers.IO) {
        api.getOrders(type = null)
    }

    // Зміна статусу конкретної страви
    suspend fun updateItemStatus(itemId: Int, status: Int) = withContext(Dispatchers.IO) {
        val req = UpdateItemStatusRequest(status)
        val response = api.updateItemStatus(itemId, req)
        if (!response.isSuccessful) {
            throw Exception("Failed to update item status: ${response.message()}")
        }
    }
}