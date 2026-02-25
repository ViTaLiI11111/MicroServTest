package com.waiter.app.data.dto

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val userId: Int,
    val username: String,
    val stationId: Int? = null
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val fullName: String?,
    val phone: String? = null,
    val email: String? = null,
    val stationId: Int? = null
)