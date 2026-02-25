package com.example.ukrainianstylerestaurant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TableSelectActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private EditText etTableNumber;
    private Button btnConfirmTable;
    private Button btnLogout;
    private Button btnScanQr;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int SCAN_REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_select);

        tvWelcome = findViewById(R.id.tv_welcome);
        etTableNumber = findViewById(R.id.et_table_number);
        btnConfirmTable = findViewById(R.id.btn_confirm_table);
        btnLogout = findViewById(R.id.btn_logout);
        btnScanQr = findViewById(R.id.btn_scan_qr);

        String username = LocalStorage.getUsername(this);
        tvWelcome.setText("Вітаємо, " + username + "!");

        int savedTableNo = LocalStorage.getTableNumber(this);
        if (savedTableNo > 0) {
            etTableNumber.setText(String.valueOf(savedTableNo));
        }

        btnScanQr.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.CAMERA },
                        CAMERA_PERMISSION_CODE);
            } else {
                startScanning();
            }
        });

        btnConfirmTable.setOnClickListener(v -> {
            String tableNoStr = etTableNumber.getText().toString().trim();

            if (tableNoStr.isEmpty()) {
                Toast.makeText(this, "Будь ласка, введіть номер столика або відскануйте QR", Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                tableNoStr = tableNoStr.replaceAll("[^0-9]", "");

                int tableNo = Integer.parseInt(tableNoStr);
                if (tableNo <= 0) {
                    Toast.makeText(this, "Номер столика має бути позитивним", Toast.LENGTH_SHORT).show();
                    return;
                }

                LocalStorage.saveTableNumber(this, tableNo);

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некоректний формат номера (очікується число)", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            LocalStorage.logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void startScanning() {
        Intent intent = new Intent(this, ScannerActivity.class);
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "Для сканування потрібен дозвіл на камеру", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String scannedData = data.getStringExtra("SCANNED_TABLE_ID");
                etTableNumber.setText(scannedData);
                Toast.makeText(this, "Код скановано: " + scannedData, Toast.LENGTH_SHORT).show();
            }
        }
    }
}