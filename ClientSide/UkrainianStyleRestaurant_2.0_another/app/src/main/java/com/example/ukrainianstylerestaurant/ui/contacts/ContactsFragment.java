package com.example.ukrainianstylerestaurant.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ukrainianstylerestaurant.R;

public class ContactsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Використовуємо оновлений layout
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Тут міг бути код для:
        // - Встановлення обробників кліків на номери телефонів (для дзвінка)
        // - Завантаження даних, якщо вони були динамічними

        // Оскільки в ContactsFragment.java логіки не було, цей Fragment залишається чистим.
    }

    // Всі методи aboutUs(), mainPage() більше не потрібні,
    // оскільки навігація обробляється DrawerLayout у MainActivity.
}