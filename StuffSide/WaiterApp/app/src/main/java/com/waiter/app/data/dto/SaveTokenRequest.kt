package com.waiter.app.data.dto

data class SaveTokenRequest(
    val username: String,
    val role: String, // "Waiter", "Courier", "Cook"
    val token: String
)