package com.waiter.app.data.dto

data class DeliveryDto(
    val id: Int,
    val orderId: String,       // Guid приходить як String
    val courierId: Int?,
    val status: Int,           // Enum (0=Created, 1=Assigned...)
    val clientAddress: String,
    val clientPhone: String?
)