package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.ClientProfileResponse;
import com.example.ukrainianstylerestaurant.model.LoginRequest;
import com.example.ukrainianstylerestaurant.model.LoginResponse;
import com.example.ukrainianstylerestaurant.model.RegisterRequest;
import com.example.ukrainianstylerestaurant.model.SaveTokenRequest;
import com.example.ukrainianstylerestaurant.model.UpdateProfileRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthApi {

    @POST("api/client/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/client/register")
    Call<ResponseBody> register(@Body RegisterRequest request);

    @PUT("api/client/profile")
    Call<ResponseBody> updateProfile(@Body UpdateProfileRequest request);

    @POST("api/notifications/token")
    Call<ResponseBody> saveToken(@Body SaveTokenRequest request);

    @GET("api/client/{id}")
    Call<ClientProfileResponse> getProfile(@Path("id") int id);
}