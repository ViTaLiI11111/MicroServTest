package com.example.ukrainianstylerestaurant;

import android.content.Intent;
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
import com.example.ukrainianstylerestaurant.model.LoginResponse;
import com.google.firebase.messaging.FirebaseMessaging; // <--- Не забудьте цей імпорт

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private ProgressBar progressBar;

    private AuthRepository authRepository;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Якщо вже залогінені - переходимо далі
        if (LocalStorage.isLoggedIn(this)) {
            startActivity(new Intent(this, TableSelectActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Будь ласка, заповніть всі поля");
                return;
            }
            performLogin(username, password);
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void performLogin(String username, String password) {
        setLoading(true);

        executorService.execute(() -> {
            try {
                // Виконуємо запит на логін
                LoginResponse response = authRepository.login(username, password);

                mainThreadHandler.post(() -> {
                    setLoading(false);
                    if (response != null) {
                        showToast("Вхід успішний!");

                        // 1. Зберігаємо сесію
                        LocalStorage.saveLoginSession(this, response.userId, response.username);

                        // 2. --- ОТРИМУЄМО ТА ВІДПРАВЛЯЄМО ТОКЕН ---
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        // Якщо не вдалося отримати токен, просто ігноруємо,
                                        // додаток все одно працюватиме, просто без пушів.
                                        return;
                                    }

                                    // Отримуємо новий токен
                                    String token = task.getResult();

                                    // Відправляємо його на сервер
                                    // "Client" - це роль цього додатку
                                    authRepository.sendTokenToServer(response.username, "Client", token);
                                });
                        // -------------------------------------------

                        // 3. Переходимо до вибору столика
                        Intent intent = new Intent(this, TableSelectActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("Неправильний логін або пароль");
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
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}