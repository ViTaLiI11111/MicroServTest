package com.waiter.app.core

object AppConfig {
    const val GLOBAL_BASE_URL = "https://unsuburbed-omar-dioptrically.ngrok-free.dev/"

    const val ORDER_BASE_URL = GLOBAL_BASE_URL
    const val AUTH_BASE_URL = GLOBAL_BASE_URL
    const val DELIVERY_BASE_URL = GLOBAL_BASE_URL

    const val SSE_URL = "${GLOBAL_BASE_URL}orders/stream"
}