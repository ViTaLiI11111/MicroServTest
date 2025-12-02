package com.waiter.app.domain.model

data class UiOrder(
    val id: String,
    val tableNo: Int,
    val status: String,
    val total: Double,
    val clientName: String,
    val isPaid: Boolean,
    val items: List<UiOrderItem>
)

data class UiOrderItem(
    val dishTitle: String,
    val qty: Int,
    val price: Double,
    // --- НОВЕ ПОЛЕ ---
    val itemStatus: String // "Pending", "Cooking", "Ready"
)