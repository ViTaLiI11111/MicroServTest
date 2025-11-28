package com.example.ukrainianstylerestaurant.ui.profile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.data.AuthRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Знаходимо поле з іменем (те, що на скріншоті)
        EditText etName = view.findViewById(R.id.et_profile_name);

        EditText etEmail = view.findViewById(R.id.et_profile_email);
        EditText etPhone = view.findViewById(R.id.et_profile_phone);
        EditText etAddress = view.findViewById(R.id.et_profile_address);
        Button btnSave = view.findViewById(R.id.btn_save_profile);

        // Підтягуємо старі дані
        etName.setText(LocalStorage.getClientName(requireContext()));
        etEmail.setText(LocalStorage.getClientEmail(requireContext()));
        etPhone.setText(LocalStorage.getClientPhone(requireContext()));
        etAddress.setText(LocalStorage.getClientAddress(requireContext()));

        // Якщо в LocalStorage пусто, підставляємо логін як заглушку
        if (etName.getText().toString().isEmpty()) {
            etName.setText(LocalStorage.getUsername(requireContext()));
        }

        btnSave.setOnClickListener(v -> {
            // 2. Беремо текст, який ввів користувач ("Vitaliy" або щось нове)
            String newName = etName.getText().toString().trim();

            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (newName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Ім'я та Телефон обов'язкові", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSave.setEnabled(false);
            btnSave.setText("Збереження...");

            int userId = LocalStorage.getUserId(requireContext());

            executorService.execute(() -> {
                try {
                    AuthRepository repo = new AuthRepository();

                    // 3. Відправляємо newName (ім'я) на сервер
                    boolean success = repo.updateProfile(userId, newName, email, phone);

                    mainHandler.post(() -> {
                        btnSave.setEnabled(true);
                        btnSave.setText("Зберегти зміни");

                        if (success) {
                            // 4. Якщо сервер прийняв, зберігаємо це ім'я локально
                            LocalStorage.saveProfile(requireContext(), newName, phone, email, address);
                            Toast.makeText(requireContext(), "Ім'я оновлено!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Помилка сервера", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mainHandler.post(() -> {
                        btnSave.setEnabled(true);
                        btnSave.setText("Зберегти зміни");
                        Toast.makeText(requireContext(), "Помилка мережі", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        return view;
    }
}