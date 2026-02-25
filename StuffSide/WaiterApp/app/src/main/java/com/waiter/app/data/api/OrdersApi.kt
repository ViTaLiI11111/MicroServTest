package com.waiter.app.data.api

import com.waiter.app.data.dto.OrderDto
import com.waiter.app.data.dto.SetStatusRequest
import com.waiter.app.data.dto.UpdateItemStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrdersApi {
    @GET("orders")
    suspend fun getOrders(
        @Query("type") type: String? = null,
        @Query("waiterId") waiterId: Int? = null,
        @Query("onlyFree") onlyFree: Boolean? = null
    ): List<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): OrderDto

    @POST("orders/{id}/pay")
    suspend fun payOrder(@Path("id") id: String): Response<Unit>


    @POST("orders/{id}/assign")
    suspend fun assignOrder(
        @Path("id") id: String,
        @Query("waiterId") waiterId: Int
    ): Response<OrderDto>

    @POST("orders/{id}/complete")
    suspend fun completeOrder(@Path("id") id: String): Response<Unit>


    @PATCH("orders/items/{itemId}/status")
    suspend fun updateItemStatus(
        @Path("itemId") itemId: Int,
        @Body req: UpdateItemStatusRequest
    ): Response<Unit>

    @POST("orders/{id}/status")
    suspend fun setStatus(
        @Path("id") id: String,
        @Body req: SetStatusRequest
    ): Response<Unit>
}