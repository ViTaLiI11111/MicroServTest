package com.waiter.app.data.api

import com.waiter.app.data.dto.DeliveryDto
import com.waiter.app.data.dto.UpdateStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DeliveryApi {
    // Отримати всі вільні замовлення (для кур'єра)
    @GET("api/deliveries/available")
    suspend fun getAvailableDeliveries(): List<DeliveryDto>

    // Отримати мої активні доставки
    @GET("api/deliveries/my/{courierId}")
    suspend fun getMyDeliveries(@Path("courierId") courierId: Int): List<DeliveryDto>

    // Взяти замовлення в роботу
    // Використовуємо POST з query параметром, як в контролері C#
    @POST("api/deliveries/{id}/take")
    suspend fun takeDelivery(
        @Path("id") id: Int,
        @Query("courierId") courierId: Int
    ): Response<Unit>

    @PUT("api/deliveries/{id}/status")
    suspend fun updateStatus(@Path("id") id: Int, @Body request: UpdateStatusRequest): Response<Unit>
}