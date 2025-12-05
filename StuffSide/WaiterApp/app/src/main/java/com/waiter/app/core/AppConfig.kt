package com.waiter.app.core

object AppConfig {
    // ВСТАВ СВІЙ ДОМЕН NGROK ТУТ (без слеша в кінці, якщо так використовується в коді, або зі слешем)
    // Оскільки в RetrofitFactory зазвичай base url має бути з /, давай напишемо з /
    const val GLOBAL_BASE_URL = "https://unsuburbed-omar-dioptrically.ngrok-free.dev/"

    // Тепер всі посилаються на одну адресу
    const val ORDER_BASE_URL = GLOBAL_BASE_URL
    const val AUTH_BASE_URL = GLOBAL_BASE_URL
    const val DELIVERY_BASE_URL = GLOBAL_BASE_URL

    const val SSE_URL = "${GLOBAL_BASE_URL}orders/stream"
}