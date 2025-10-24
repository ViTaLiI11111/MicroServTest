package com.example.ukrainianstylerestaurant.data;

import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.net.OrdersApi;
import com.example.ukrainianstylerestaurant.net.OrdersRetrofitClient;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OrdersRepository {

    private final OrdersApi api = OrdersRetrofitClient.get().create(OrdersApi.class);

    public boolean createOrder(CreateOrderRequest request) throws IOException {
        Response<ResponseBody> r = api.createOrder(request).execute();
        return r.isSuccessful();
    }
}
