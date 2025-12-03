package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query; // <--- Не забудь цей імпорт

public interface OrdersApi {

    @POST("orders")
    Call<OrderResponse> createOrder(@Body CreateOrderRequest request);

    @POST("orders/{id}/pay")
    Call<ResponseBody> payOrder(@Path("id") String id);

    @GET("orders/{id}")
    Call<OrderResponse> getOrder(@Path("id") String id);

    // --- НОВИЙ МЕТОД ---
    @GET("orders")
    Call<List<OrderResponse>> getClientHistory(@Query("clientName") String clientName);
}