package com.waiter.app.domain.model

data class UiOrderItem(
    val dishTitle: String,
    val qty: Int,
    val price: Double
)

data class UiOrder(
    val id: String,
    val tableNo: Int,
    val status: String,
    val total: Double,
    val clientName: String,
    val items: List<UiOrderItem>
)
