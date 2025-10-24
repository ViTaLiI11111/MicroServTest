package com.waiter.app.data.api

import com.waiter.app.data.dto.OrderDto
import com.waiter.app.data.dto.SetStatusRequest
import retrofit2.http.*

interface OrdersApi {
    @GET("api/orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto
}

