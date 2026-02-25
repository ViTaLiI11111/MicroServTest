package com.waiter.app.data.dto

data class SaveTokenRequest(
    val username: String,
    val role: String,
    val token: String
)