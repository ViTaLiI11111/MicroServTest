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
    private Handler mainThreadHandler; // Для оновлення UI з фонового потоку

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Перевіряємо, чи юзер ВЖЕ залогінений
        if (LocalStorage.isLoggedIn(this)) {
            // Якщо так, одразу переходимо до вибору столика
            startActivity(new Intent(this, TableSelectActivity.class));
            finish(); // Закриваємо LoginActivity
            return; // Не завантажуємо layout
        }

        setContentView(R.layout.activity_login);

        // Ініціалізація
        authRepository = new AuthRepository();
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        // Пошук View
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);
        progressBar = findViewById(R.id.progress_bar);

        // Обробник кнопки "Увійти"
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Будь ласка, заповніть всі поля");
                return;
            }
            performLogin(username, password);
        });

        // Обробник тексту "Зареєструватись"
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void performLogin(String username, String password) {
        setLoading(true);

        executorService.execute(() -> {
            try {
                // Виконуємо запит в фоновому потоці
                LoginResponse response = authRepository.login(username, password);

                // Повертаємось на головний потік для оновлення UI
                mainThreadHandler.post(() -> {
                    setLoading(false);
                    if (response != null) {
                        // Успішний логін
                        showToast("Вхід успішний!");
                        // Зберігаємо сесію
                        LocalStorage.saveLoginSession(this, response.userId, response.username);
                        // Переходимо до вибору столика
                        Intent intent = new Intent(this, TableSelectActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Неправильний логін/пароль (401) або інша помилка
                        showToast("Неправильний логін або пароль");
                    }
                });
            } catch (Exception e) {
                // Мережева помилка
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