package com.waiter.app.data.dto

data class UpdateStatusRequest(
    val courierId: Int,
    val newStatus: Int // 2 = PickedUp, 3 = Delivered
)