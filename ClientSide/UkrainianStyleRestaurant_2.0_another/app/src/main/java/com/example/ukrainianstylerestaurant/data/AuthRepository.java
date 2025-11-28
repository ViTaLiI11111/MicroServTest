package com.example.ukrainianstylerestaurant.data;

import com.example.ukrainianstylerestaurant.model.LoginRequest;
import com.example.ukrainianstylerestaurant.model.LoginResponse;
import com.example.ukrainianstylerestaurant.model.RegisterRequest;
import com.example.ukrainianstylerestaurant.model.UpdateProfileRequest;
import com.example.ukrainianstylerestaurant.net.AuthApi;
import com.example.ukrainianstylerestaurant.net.AuthRetrofitClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi api = AuthRetrofitClient.get().create(AuthApi.class);

    /**
     * Спроба увійти. Повертає LoginResponse при успіху або null при невдачі.
     */
    public LoginResponse login(String username, String password) throws IOException {
        Response<LoginResponse> r = api.login(new LoginRequest(username, password)).execute();

        // Повертаємо тіло, тільки якщо запит успішний (2xx)
        if (r.isSuccessful()) {
            return r.body();
        }
        // Якщо 401 (Unauthorized) або інша помилка, повертаємо null
        return null;
    }

    /**
     * Спроба реєстрації. Повертає true при успіху.
     */
    public boolean register(String username, String password, String email, String phone) throws IOException {
        Response<ResponseBody> r = api.register(
                new RegisterRequest(username, password, email, phone)
        ).execute();

        return r.isSuccessful();
    }

    public boolean updateProfile(int userId, String fullName, String email, String phone) throws IOException {
        // Передаємо fullName (ваше "Vitaliy") у запит
        UpdateProfileRequest req = new UpdateProfileRequest(userId, fullName, email, phone);

        retrofit2.Response<okhttp3.ResponseBody> r = api.updateProfile(req).execute();
        return r.isSuccessful();
    }
}