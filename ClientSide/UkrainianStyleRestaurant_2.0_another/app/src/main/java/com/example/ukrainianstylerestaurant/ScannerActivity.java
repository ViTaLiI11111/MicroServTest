package com.example.ukrainianstylerestaurant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton; // Додали імпорт
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScannerActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);

        // --- ЛОГІКА КНОПКИ НАЗАД ---
        ImageButton btnBack = findViewById(R.id.btn_back_scanner);
        btnBack.setOnClickListener(v -> {
            // Просто закриваємо цю Activity, і користувач повертається на попередню
            finish();
        });
        // ---------------------------

        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(com.budiyev.android.codescanner.AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(com.budiyev.android.codescanner.ScanMode.SINGLE);
        mCodeScanner.setTouchFocusEnabled(true);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(() -> {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("SCANNED_TABLE_ID", result.getText());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                });
            }
        });

        mCodeScanner.setErrorCallback(error -> runOnUiThread(
                () -> Toast.makeText(ScannerActivity.this, "Помилка камери: " + error.getMessage(), Toast.LENGTH_LONG).show()));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}