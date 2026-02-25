package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.DeliveryStatusResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DeliveryApi {
    @GET("api/deliveries/track/{orderId}")
    Call<DeliveryStatusResponse> getDeliveryStatus(@Path("orderId") String orderId);
}