package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET; // Можливо вже є
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrdersApi {

    @POST("orders")
    Call<OrderResponse> createOrder(@Body CreateOrderRequest request);

    // --- ДОДАТИ ЦЕЙ МЕТОД ---
    @POST("orders/{id}/pay")
    Call<ResponseBody> payOrder(@Path("id") String id);

    // --- ЦЕЙ МЕТОД ПОТРІБЕН ДЛЯ ПЕРЕВІРКИ СТАТУСУ ОПЛАТИ ---
    @GET("orders/{id}")
    Call<OrderResponse> getOrder(@Path("id") String id);
}