package com.waiter.app.domain.model

data class UiOrder(
    val id: String,
    val tableNo: Int,
    val status: String,
    val total: Double,
    val clientName: String,
    val isPaid: Boolean,

    // --- ДОДАЛИ ---
    val waiterId: Int?,

    val items: List<UiOrderItem>
)

data class UiOrderItem(
    val dishTitle: String,
    val qty: Int,
    val price: Double,
    val itemStatus: String // "Pending", "Cooking", "Ready"
)