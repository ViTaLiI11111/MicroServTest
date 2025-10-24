package com.waiter.app.data.repo.api

import com.waiter.app.data.dto.OrderDto
import retrofit2.http.GET
import retrofit2.http.Path

interface OrdersApi {
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto
}
