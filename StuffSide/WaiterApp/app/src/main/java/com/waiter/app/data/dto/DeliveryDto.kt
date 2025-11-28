package com.waiter.app.data.dto

data class DeliveryDto(
    val id: Int,
    val orderId: String,
    val courierId: Int?,
    val status: Int, // 0=Created, 1=Assigned, 2=PickedUp, 3=Delivered
    val clientAddress: String,
    val clientPhone: String?,
    val clientName: String? // <--- НОВЕ ПОЛЕ
)