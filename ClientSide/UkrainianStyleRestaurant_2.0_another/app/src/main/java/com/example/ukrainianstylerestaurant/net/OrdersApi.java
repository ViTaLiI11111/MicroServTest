package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrdersApi {

    @POST("orders")
    Call<OrderResponse> createOrder(@Body CreateOrderRequest request);
}
