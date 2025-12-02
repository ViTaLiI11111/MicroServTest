package com.waiter.app.data.repo

import com.waiter.app.core.Result
import com.waiter.app.core.UserRole
import com.waiter.app.data.api.AuthApi
import com.waiter.app.data.dto.LoginRequest
import com.waiter.app.data.dto.LoginResponse
import com.waiter.app.data.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val api: AuthApi = RetrofitModule.createAuthApi()
) {

    suspend fun login(role: UserRole, username: String, pass: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val req = LoginRequest(username, pass)

            // ВИПРАВЛЕНО: Використовуємо when для 3-х ролей
            val response = when (role) {
                UserRole.WAITER -> api.loginWaiter(req)
                UserRole.COURIER -> api.loginCourier(req)
                UserRole.COOK -> api.loginCook(req)
            }

            if (response.isSuccessful && response.body() != null) {
                Result.Ok(response.body()!!)
            } else {
                Result.Err(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Err(e)
        }
    }

    suspend fun register(
        role: UserRole,
        username: String,
        pass: String,
        fullName: String,
        phone: String,
        email: String,
        stationId: Int? // Додайте stationId сюди, якщо він передається
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Формуємо запит
            val req = RegisterRequest(
                username = username,
                password = pass,
                fullName = fullName,
                phone = phone,
                email = email,
                stationId = stationId // Передаємо stationId
            )

            // ВИПРАВЛЕНО: Використовуємо when для 3-х ролей
            val response = when (role) {
                UserRole.WAITER -> api.registerWaiter(req)
                UserRole.COURIER -> api.registerCourier(req)
                UserRole.COOK -> api.registerCook(req)
            }

            if (response.isSuccessful) {
                Result.Ok(Unit)
            } else {
                Result.Err(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Err(e)
        }
    }
}