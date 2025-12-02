package com.waiter.app.data.dto

data class OrderItemDto(
    val id: Int,       // ID самої позиції (важливо для зміни статусу)
    val dishId: Int,
    val dishTitle: String?,
    val qty: Int,
    val price: Double,
    // --- НОВІ ПОЛЯ ---
    val stationId: Int,  // ID цеху (1=Гарячий, 2=Холодний...)
    val status: String   // "Pending", "Cooking", "Ready"
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

    // --- НОВІ ПОЛЯ ---
    val isPaid: Boolean,
    val paidAt: String?,

    val items: List<OrderItemDto>
)

