package com.waiter.app.data.dto

data class DeliveryDto(
    val id: Int,
    val orderId: String,
    val courierId: Int?,
    val status: Int,
    val clientAddress: String,
    val clientPhone: String?,
    val clientName: String?,
    val isReadyForPickup: Boolean,

    val isPaid: Boolean,
    val total: Double
)