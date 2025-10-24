package com.waiter.app.ui.orders

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
    val items: List<UiOrderItem>
)
