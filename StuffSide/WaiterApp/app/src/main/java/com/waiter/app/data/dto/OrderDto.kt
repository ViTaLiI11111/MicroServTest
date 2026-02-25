package com.waiter.app.data.dto

data class OrderItemDto(
    val id: Int,
    val dishId: Int,
    val dishTitle: String?,
    val qty: Int,
    val price: Double,
    val stationId: Int,
    val status: String
)

data class OrderDto(
    val id: String,
    val tableNo: Int,
    val status: String,
    val type: String?,
    val total: Double,
    val createdAt: String?,
    val deliveryAddress: String?,
    val clientPhone: String?,
    val clientName: String?,
    val isPaid: Boolean,
    val paidAt: String?,

    val waiterId: Int?,

    val items: List<OrderItemDto>
)