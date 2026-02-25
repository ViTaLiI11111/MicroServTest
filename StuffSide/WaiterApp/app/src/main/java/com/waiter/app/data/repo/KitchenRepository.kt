package com.waiter.app.data.repo

import com.waiter.app.data.dto.OrderDto
import com.waiter.app.data.dto.UpdateItemStatusRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KitchenRepository {
    private val api = RetrofitModule.createApi()

    suspend fun getOrders(): List<OrderDto> = withContext(Dispatchers.IO) {
        api.getOrders(type = null)
    }

    suspend fun updateItemStatus(itemId: Int, status: Int) = withContext(Dispatchers.IO) {
        val req = UpdateItemStatusRequest(status)
        val response = api.updateItemStatus(itemId, req)
        if (!response.isSuccessful) {
            throw Exception("Failed to update item status: ${response.message()}")
        }
    }
}