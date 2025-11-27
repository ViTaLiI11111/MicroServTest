package com.waiter.app.data.repo

import com.waiter.app.BuildConfig
import com.waiter.app.core.AppConfig
import com.waiter.app.data.api.AuthApi
import com.waiter.app.data.api.DeliveryApi // Додано імпорт
import com.waiter.app.data.repo.api.OrdersApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitModule {

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // --- Order Service ---
    fun createApi(): OrdersApi {
        val baseUrl = if (BuildConfig.API_BASE_URL.endsWith("/"))
            BuildConfig.API_BASE_URL
        else
            BuildConfig.API_BASE_URL + "/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(OrdersApi::class.java)
    }

    // --- Auth Service ---
    fun createAuthApi(): AuthApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.AUTH_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(AuthApi::class.java)
    }

    // --- Delivery Service (Новий метод) ---
    fun createDeliveryApi(): DeliveryApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.DELIVERY_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(DeliveryApi::class.java)
    }
}