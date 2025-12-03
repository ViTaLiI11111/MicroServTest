package com.example.ukrainianstylerestaurant.data;

import android.util.Log;

import com.example.ukrainianstylerestaurant.model.ClientProfileResponse;
import com.example.ukrainianstylerestaurant.model.LoginRequest;
import com.example.ukrainianstylerestaurant.model.LoginResponse;
import com.example.ukrainianstylerestaurant.model.RegisterRequest;
import com.example.ukrainianstylerestaurant.model.SaveTokenRequest;
import com.example.ukrainianstylerestaurant.model.UpdateProfileRequest;
import com.example.ukrainianstylerestaurant.net.AuthApi;
import com.example.ukrainianstylerestaurant.net.AuthRetrofitClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi api = AuthRetrofitClient.get().create(AuthApi.class);

    public LoginResponse login(String username, String password) throws IOException {
        Response<LoginResponse> r = api.login(new LoginRequest(username, password)).execute();
        if (r.isSuccessful()) {
            return r.body();
        }
        return null;
    }

    public boolean register(String username, String password, String email, String phone) throws IOException {
        Response<ResponseBody> r = api.register(
                new RegisterRequest(username, password, email, phone)
        ).execute();
        return r.isSuccessful();
    }

    public boolean updateProfile(int userId, String fullName, String email, String phone, String address) throws IOException {
        UpdateProfileRequest req = new UpdateProfileRequest(userId, fullName, email, phone, address);
        Response<ResponseBody> r = api.updateProfile(req).execute();
        return r.isSuccessful();
    }

    // --- НОВИЙ МЕТОД ---
    // Відправляємо асинхронно, бо це не блокує UI
    public void sendTokenToServer(String username, String role, String token) {
        api.saveToken(new SaveTokenRequest(username, role, token)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("AuthRepo", "Token sent successfully");
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("AuthRepo", "Failed to send token: " + t.getMessage());
            }
        });
    }

    public ClientProfileResponse getProfile(int userId) throws IOException {
        Response<ClientProfileResponse> r = api.getProfile(userId).execute();
        if (r.isSuccessful()) {
            return r.body();
        }
        return null;
    }
}