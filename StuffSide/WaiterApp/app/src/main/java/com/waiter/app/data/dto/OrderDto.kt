package com.waiter.app.data.dto

data class OrderItemDto(
    val id: Int,
    val dishId: Int,
    val dishTitle: String?,
    val qty: Int,
    val price: Double,
    val stationId: Int,
    val status: String // "Pending", "Cooking", "Ready"
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

    // --- НОВЕ ПОЛЕ ---
    val waiterId: Int?, // Може бути null, якщо замовлення ще ніхто не взяв

    val items: List<OrderItemDto>
)