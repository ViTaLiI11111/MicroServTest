package com.example.ukrainianstylerestaurant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ukrainianstylerestaurant.data.AuthRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etPhone;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btn_register);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);
        progressBar = findViewById(R.id.progress_bar);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Логін та Пароль є обов'язковими");
                return;
            }
            performRegister(username, password, email, phone);
        });

        tvGoToLogin.setOnClickListener(v -> {
            // Просто закриваємо цей екран, повертаючись до LoginActivity
            finish();
        });
    }

    private void performRegister(String username, String password, String email, String phone) {
        setLoading(true);

        executorService.execute(() -> {
            try {
                boolean success = authRepository.register(username, password, email, phone);

                mainThreadHandler.post(() -> {
                    setLoading(false);
                    if (success) {
                        showToast("Реєстрація успішна! Тепер увійдіть.");
                        finish(); // Повертаємось до LoginActivity
                    } else {
                        showToast("Помилка: цей логін вже зайнятий");
                    }
                });
            } catch (Exception e) {
                mainThreadHandler.post(() -> {
                    setLoading(false);
                    showToast("Помилка мережі: " + e.getMessage());
                });
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}