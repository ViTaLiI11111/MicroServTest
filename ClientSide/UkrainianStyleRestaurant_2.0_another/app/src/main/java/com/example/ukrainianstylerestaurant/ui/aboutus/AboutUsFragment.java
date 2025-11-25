package com.example.ukrainianstylerestaurant.ui.aboutus; // Створіть новий пакет 'ui.aboutus'

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ukrainianstylerestaurant.R;

public class AboutUsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Використовуємо layout, який раніше належав AboutUs Activity
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Тут ви б знайшли TextView та інший вміст і встановили дані,
        // але оскільки в AboutUs Activity не було багато логіки,
        // тут зазвичай нічого не потрібно, якщо вся логіка в XML.

        // ВАЖЛИВО: Видаліть всі посилання на "constraintLayout" з бічною панеллю,
        // оскільки вона тепер знаходиться у MainActivity.
        // Видаліть також onClick методи "goToContacts", "mainPage" і т.д.
        // Вони будуть замінені Navigation Component.
    }
}