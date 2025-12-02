package com.waiter.app.data.api

import com.waiter.app.data.dto.LoginRequest
import com.waiter.app.data.dto.LoginResponse
import com.waiter.app.data.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    // ... Waiter methods ...
    @POST("api/waiter/login")
    suspend fun loginWaiter(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/waiter/register")
    suspend fun registerWaiter(@Body request: RegisterRequest): Response<Unit>

    // ... Courier methods ...
    @POST("api/courier/login")
    suspend fun loginCourier(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/courier/register")
    suspend fun registerCourier(@Body request: RegisterRequest): Response<Unit>

    // --- НОВІ МЕТОДИ КУХАРЯ ---
    @POST("api/cook/login")
    suspend fun loginCook(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/cook/register")
    suspend fun registerCook(@Body request: RegisterRequest): Response<Unit>
}