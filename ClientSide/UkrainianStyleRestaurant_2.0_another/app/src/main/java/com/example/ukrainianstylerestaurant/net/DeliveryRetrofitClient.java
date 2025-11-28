package com.example.ukrainianstylerestaurant.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class DeliveryRetrofitClient {
    private static Retrofit retrofit;

    // ВАЖЛИВО: Порт 5300 (як у docker-compose для deliveryservice)
    // 10.0.2.2 - це "localhost" комп'ютера для емулятора Android
    private static final String BASE_URL = "http://10.0.2.2:5300/";

    public static Retrofit get() {
        if (retrofit == null) {
            OkHttpClient http = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(http)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private DeliveryRetrofitClient() {}
}