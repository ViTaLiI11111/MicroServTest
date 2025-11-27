package com.waiter.app.core

object AppConfig {
    // URL для замовлень
    const val ORDER_BASE_URL = "http://10.0.2.2:5245"

    // URL для авторизації (переконався, що порт 5210, як ми вирішили раніше)
    const val AUTH_BASE_URL = "http://10.0.2.2:5210"

    // URL для доставки (новий сервіс)
    const val DELIVERY_BASE_URL = "http://10.0.2.2:5300"

    const val SSE_URL = "$ORDER_BASE_URL/orders/stream"
}