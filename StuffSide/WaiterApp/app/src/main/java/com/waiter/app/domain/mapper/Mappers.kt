package com.waiter.app.domain.mapper

import com.waiter.app.data.dto.OrderDto
import com.waiter.app.data.dto.OrderItemDto
import com.waiter.app.domain.model.UiOrder
import com.waiter.app.domain.model.UiOrderItem

fun OrderItemDto.toUi(): UiOrderItem =
    UiOrderItem(
        dishTitle = dishTitle ?: "Dish #$dishId",
        qty = qty,
        price = price,
        itemStatus = status ?: "Pending"
    )

fun OrderDto.toUi(): UiOrder =
    UiOrder(
        id = id,
        tableNo = tableNo,
        status = status,
        total = total,
        clientName = clientName ?: "Гість",
        isPaid = isPaid,

        // --- МАПИНГ ---
        waiterId = waiterId,

        items = items.map { it.toUi() }
    )