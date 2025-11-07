package com.example.ukrainianstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TableSelectActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private EditText etTableNumber;
    private Button btnConfirmTable;
    private Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_select);

        tvWelcome = findViewById(R.id.tv_welcome);
        etTableNumber = findViewById(R.id.et_table_number);
        btnConfirmTable = findViewById(R.id.btn_confirm_table);
        btnLogout = findViewById(R.id.btn_logout);

        // Встановлюємо привітання
        String username = LocalStorage.getUsername(this);
        tvWelcome.setText("Вітаємо, " + username + "!");

        // Встановлюємо збережений номер столика, якщо він є
        int savedTableNo = LocalStorage.getTableNumber(this);
        if (savedTableNo > 0) {
            etTableNumber.setText(String.valueOf(savedTableNo));
        }

        btnConfirmTable.setOnClickListener(v -> {
            String tableNoStr = etTableNumber.getText().toString().trim();
            if (tableNoStr.isEmpty()) {
                Toast.makeText(this, "Будь ласка, введіть номер столика", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int tableNo = Integer.parseInt(tableNoStr);
                if (tableNo <= 0) {
                    Toast.makeText(this, "Номер столика має бути позитивним", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Зберігаємо номер столика
                LocalStorage.saveTableNumber(this, tableNo);

                // Переходимо до головного меню (MainActivity)
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некоректний номер столика", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            // Вихід
            LocalStorage.logout(this);
            // Повертаємось на екран логіну
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}