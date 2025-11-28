package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.DeliveryStatusResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DeliveryApi {
    // Запит статусу за ID замовлення (GUID)
    // Зверніть увагу: на сервері це /api/deliveries/track/{orderId}
    @GET("api/deliveries/track/{orderId}")
    Call<DeliveryStatusResponse> getDeliveryStatus(@Path("orderId") String orderId);
}