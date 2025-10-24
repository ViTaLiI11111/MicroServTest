package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrdersApi {

    @POST("orders")
    Call<ResponseBody> createOrder(@Body CreateOrderRequest request);
}
