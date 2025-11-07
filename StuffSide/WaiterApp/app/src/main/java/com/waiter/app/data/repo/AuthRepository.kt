package com.waiter.app.data.repo

import com.waiter.app.core.Result
import com.waiter.app.data.api.AuthApi
import com.waiter.app.data.dto.LoginRequest
import com.waiter.app.data.dto.LoginResponse
import com.waiter.app.data.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val api: AuthApi = RetrofitModule.createAuthApi()
) {

    // Оновлюємо: тепер повертає Result<LoginResponse>
    suspend fun login(username: String, password: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Result.Ok(response.body()!!)
            } else {
                Result.Err(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Err(e)
        }
    }

    suspend fun register(username: String, password: String, fullName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequest(username, password, fullName))
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