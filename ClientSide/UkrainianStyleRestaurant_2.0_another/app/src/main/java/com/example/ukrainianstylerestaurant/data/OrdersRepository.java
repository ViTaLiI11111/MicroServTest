package com.example.ukrainianstylerestaurant.data;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import com.example.ukrainianstylerestaurant.net.OrdersApi;
import com.example.ukrainianstylerestaurant.net.OrdersRetrofitClient;
import java.io.IOException;
import java.util.List;
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

    public boolean payOrder(String orderId) throws IOException {
        Response<ResponseBody> r = api.payOrder(orderId).execute();
        return r.isSuccessful();
    }

    public OrderResponse getOrder(String id) throws IOException {
        Response<OrderResponse> r = api.getOrder(id).execute();
        if (r.isSuccessful()) {
            return r.body();
        }
        return null;
    }

    // --- НОВИЙ МЕТОД ---
    public List<OrderResponse> getClientHistory(String clientName) throws IOException {
        Response<List<OrderResponse>> r = api.getClientHistory(clientName).execute();
        if (r.isSuccessful()) {
            return r.body();
        }
        return null;
    }
}