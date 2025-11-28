package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.LoginRequest;
import com.example.ukrainianstylerestaurant.model.LoginResponse;
import com.example.ukrainianstylerestaurant.model.RegisterRequest;
import com.example.ukrainianstylerestaurant.model.UpdateProfileRequest; // <-- Імпорт

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT; // <-- Імпорт

public interface AuthApi {

    @POST("api/client/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/client/register")
    Call<ResponseBody> register(@Body RegisterRequest request);

    // --- НОВИЙ МЕТОД ---
    @PUT("api/client/profile")
    Call<ResponseBody> updateProfile(@Body UpdateProfileRequest request);
}