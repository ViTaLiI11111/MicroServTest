package com.waiter.app.data.dto

// DTO для /api/waiter/login
data class LoginRequest(
    val username: String,
    val password: String
)

// DTO-відповідь при успішному логіні
data class LoginResponse(
    val userId: Int,
    val username: String,
    val stationId: Int? = null
)

// DTO для /api/waiter/register
data class RegisterRequest(
    val username: String,
    val password: String,
    val fullName: String?,
    val phone: String? = null,
    val email: String? = null, // <-- ДОДАНО EMAIL
    val stationId: Int? = null
)