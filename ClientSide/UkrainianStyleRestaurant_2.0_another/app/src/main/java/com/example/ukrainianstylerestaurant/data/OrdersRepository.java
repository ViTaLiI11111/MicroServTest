package com.example.ukrainianstylerestaurant.data;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import com.example.ukrainianstylerestaurant.net.OrdersApi;
import com.example.ukrainianstylerestaurant.net.OrdersRetrofitClient;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OrdersRepository {

    private final OrdersApi api = OrdersRetrofitClient.get().create(OrdersApi.class);

    public OrderResponse createOrder(CreateOrderRequest request) throws IOException {
        Response<OrderResponse> r = api.createOrder(request).execute();
        if (r.isSuccessful()) {
            return r.body();
        }
        return null;
    }

    public OrderResponse getOrder(String id) throws IOException {
        Response<OrderResponse> r = api.getOrder(id).execute();
        if (r.isSuccessful()) {
            return r.body();
        }
        return null;
    }

    // --- МЕТОД ОПЛАТИ ---
    public boolean payOrder(String orderId) throws IOException {
        // Викликаємо POST orders/{id}/pay
        Response<ResponseBody> r = api.payOrder(orderId).execute();
        return r.isSuccessful();
    }
}