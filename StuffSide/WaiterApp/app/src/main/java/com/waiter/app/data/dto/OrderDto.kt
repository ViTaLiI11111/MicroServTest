package com.waiter.app.data.dto

data class OrderItemDto(
    val dishId: Int,
    val dishTitle: String?,
    val qty: Int,
    val price: Double
)

data class OrderDto(
    val id: String,
    val tableNo: Int,
    val status: String,
    val total: Double,
    val clientName: String?,
    val items: List<OrderItemDto>
)

