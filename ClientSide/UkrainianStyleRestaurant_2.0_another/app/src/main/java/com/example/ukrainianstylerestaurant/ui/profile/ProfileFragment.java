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
import androidx.navigation.Navigation;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.data.AuthRepository;
import com.example.ukrainianstylerestaurant.model.ClientProfileResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private EditText etName, etEmail, etPhone, etAddress;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_profile_name);
        etEmail = view.findViewById(R.id.et_profile_email);
        etPhone = view.findViewById(R.id.et_profile_phone);
        etAddress = view.findViewById(R.id.et_profile_address);
        btnSave = view.findViewById(R.id.btn_save_profile);

        fillFieldsFromLocal();

        loadProfileFromServer();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void fillFieldsFromLocal() {
        etName.setText(LocalStorage.getClientName(requireContext()));
        etEmail.setText(LocalStorage.getClientEmail(requireContext()));
        etPhone.setText(LocalStorage.getClientPhone(requireContext()));
        etAddress.setText(LocalStorage.getClientAddress(requireContext()));

        if (etName.getText().toString().isEmpty()) {
            etName.setText(LocalStorage.getUsername(requireContext()));
        }
    }

    private void loadProfileFromServer() {
        int userId = LocalStorage.getUserId(requireContext());
        if (userId == -1) return;

        executorService.execute(() -> {
            try {
                AuthRepository repo = new AuthRepository();
                ClientProfileResponse profile = repo.getProfile(userId);

                if (profile != null) {
                    mainHandler.post(() -> {
                        LocalStorage.saveProfile(requireContext(),
                                profile.fullName != null ? profile.fullName : profile.username,
                                profile.phone,
                                profile.email,
                                profile.address);

                        fillFieldsFromLocal();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveProfile() {
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
                boolean success = repo.updateProfile(userId, newName, email, phone, address);

                mainHandler.post(() -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Зберегти зміни");

                    if (success) {
                        LocalStorage.saveProfile(requireContext(), newName, phone, email, address);
                        Toast.makeText(requireContext(), "Дані збережено!", Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(requireView()).popBackStack();
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
    }
}