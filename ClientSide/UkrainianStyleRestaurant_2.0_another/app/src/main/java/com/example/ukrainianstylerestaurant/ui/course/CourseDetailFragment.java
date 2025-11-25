package com.example.ukrainianstylerestaurant.ui.course;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Order;

import java.io.File;

public class CourseDetailFragment extends Fragment {

    private int courseId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. ОТРИМАННЯ ДАНИХ З АРГУМЕНТІВ (замість Intent)
        if (getArguments() != null) {
            Bundle args = getArguments();

            // Всі дані тепер отримуємо з Bundle
            courseId = args.getInt("courseId", 0);
            int courseBgColor = args.getInt("courseBg", 0);
            String courseTitleText = args.getString("courseTitle");
            String coursePriceText = args.getString("coursePrice");
            String coursePepperText = args.getString("coursePepper");
            String imageFilePath = args.getString("courseImageFilePath");

            // 2. ПОШУК VIEW
            ImageView courseImage = view.findViewById(R.id.coursePageImage);
            TextView courseTitle = view.findViewById(R.id.coursePageTitle);
            TextView coursePrice = view.findViewById(R.id.coursePagePrice);
            TextView coursePepper = view.findViewById(R.id.coursePagePepper);
            ImageButton addToCartBtn = view.findViewById(R.id.addToCart);

            // 3. ВСТАНОВЛЕННЯ ДАНИХ
            view.findViewById(R.id.coursePageBg).setBackgroundColor(courseBgColor);
            courseTitle.setText(courseTitleText);
            coursePrice.setText(coursePriceText);
            coursePepper.setText(coursePepperText);

            // Завантаження зображення
            if (imageFilePath != null && !imageFilePath.isEmpty()) {
                File imageFile = new File(imageFilePath);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    courseImage.setImageBitmap(bitmap);
                } else {
                    courseImage.setImageResource(R.drawable.default_image);
                }
            } else {
                courseImage.setImageResource(R.drawable.default_image);
            }

            // 4. Обробник кнопки "Додати до кошика"
            addToCartBtn.setOnClickListener(this::addToCart);
        }

        // ВАЖЛИВО: Навігаційні методи mainPage(), goToContacts(), aboutUs() - ВИДАЛЕНО!
    }

    public void addToCart(View view) {
        if (courseId != 0) {
            Order.items_id.add(courseId);
            Toast.makeText(requireContext(), "Додано до кошика!", Toast.LENGTH_LONG).show();
        }
    }
}