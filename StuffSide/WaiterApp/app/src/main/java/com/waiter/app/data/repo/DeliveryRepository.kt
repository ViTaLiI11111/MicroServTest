package com.waiter.app.data.repo

import com.waiter.app.data.dto.DeliveryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeliveryRepository {
    // Створюємо API через модуль
    private val api = RetrofitModule.createDeliveryApi()

    suspend fun getAvailable(): List<DeliveryDto> = withContext(Dispatchers.IO) {
        api.getAvailableDeliveries()
    }

    suspend fun getMyDeliveries(courierId: Int): List<DeliveryDto> = withContext(Dispatchers.IO) {
        api.getMyDeliveries(courierId)
    }

    suspend fun takeDelivery(deliveryId: Int, courierId: Int) = withContext(Dispatchers.IO) {
        val response = api.takeDelivery(deliveryId, courierId)
        if (!response.isSuccessful) {
            throw Exception("Failed to take order: ${response.message()}")
        }
    }
}