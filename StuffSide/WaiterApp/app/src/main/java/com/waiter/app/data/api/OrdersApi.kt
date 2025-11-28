package com.waiter.app.data.api

import com.waiter.app.data.dto.OrderDto
import retrofit2.http.GET
import retrofit2.http.Path

interface OrdersApi {
    // БУЛО: @GET("api/orders")
    // ТРЕБА:
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    // БУЛО: @GET("api/orders/{id}")
    // ТРЕБА:
    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto
}