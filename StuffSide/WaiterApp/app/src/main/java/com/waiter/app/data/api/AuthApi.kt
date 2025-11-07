package com.waiter.app.data.api

import com.waiter.app.data.dto.LoginRequest
import com.waiter.app.data.dto.LoginResponse
import com.waiter.app.data.dto.RegisterRequest
import retrofit2.Response // Важливо: використовуємо Response для перевірки помилок
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/waiter/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/waiter/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit> // Реєстрація просто повертає 200 OK
}