package com.example.ukrainianstylerestaurant.ui.course;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton; // Імпорт для кнопки назад
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Імпорт навігації

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

        if (getArguments() != null) {
            Bundle args = getArguments();

            courseId = args.getInt("courseId", 0);
            String courseTitleText = args.getString("courseTitle");
            String coursePriceText = args.getString("coursePrice");
            String coursePepperText = args.getString("coursePepper");
            String imageFilePath = args.getString("courseImageFilePath");

            ImageView courseImage = view.findViewById(R.id.coursePageImage);
            TextView courseTitle = view.findViewById(R.id.coursePageTitle);
            TextView coursePrice = view.findViewById(R.id.coursePagePrice);
            TextView coursePepper = view.findViewById(R.id.coursePagePepper);

            Button addToCartBtn = view.findViewById(R.id.addToCart);

            ImageButton btnBack = view.findViewById(R.id.btnBack);

            courseTitle.setText(courseTitleText);
            coursePrice.setText(coursePriceText + " грн");
            coursePepper.setText(coursePepperText);

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

            addToCartBtn.setOnClickListener(v -> addToCart(v));

            btnBack.setOnClickListener(v -> {
                Navigation.findNavController(view).popBackStack();
            });
        }
    }

    public void addToCart(View view) {
        if (courseId != 0) {
            int currentQty = Order.itemsMap.getOrDefault(courseId, 0);
            Order.itemsMap.put(courseId, currentQty + 1);

            Toast.makeText(requireContext(), "Додано! (Всього: " + (currentQty + 1) + ")", Toast.LENGTH_SHORT).show();
        }
    }
}