package com.example.ukrainianstylerestaurant.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.model.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    Context context;
    List<Course> courses;

    public CourseAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Використовуємо оновлений course_item
        View courseItems = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseAdapter.CourseViewHolder(courseItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(holder.getAdapterPosition());
        if (course != null) {

            // 1. Встановлюємо колір фону (або дефолтний, якщо помилка)
            try {
                // Якщо у тебе в базі колір текстом "#FFFFFF", то parseColor спрацює
                // Але для нового дизайну краще фіксований темний фон у XML,
                // проте якщо ти хочеш динамічний, розкоментуй рядок нижче:
                // holder.courseBg.setCardBackgroundColor(Color.parseColor(course.getColor()));
            } catch (Exception e) {
                holder.courseBg.setCardBackgroundColor(Color.DKGRAY);
            }

            // 2. Обробка зображення (Base64 -> Bitmap)
            String imageBase64 = course.getImageBase64();
            Bitmap decodedByte = null;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.courseImage.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    holder.courseImage.setImageResource(R.drawable.default_image);
                }
            } else {
                holder.courseImage.setImageResource(R.drawable.default_image);
            }

            // 3. Текстові поля
            holder.courseTitle.setText(course.getTitle());
            holder.coursePrice.setText(course.getPrice() + " грн"); // Додаємо валюту
            holder.coursePepper.setText(course.getPepper() + "/5");

            // 4. Клік по самій картці (перехід до деталей)
            Bitmap finalDecodedByte = decodedByte;
            holder.itemView.setOnClickListener(view -> {
                File imageFile = null;
                if (finalDecodedByte != null) {
                    imageFile = saveImageToFile(finalDecodedByte, "course_" + course.getId() + ".png");
                }

                Bundle bundle = new Bundle();
                bundle.putInt("courseId", course.getId());
                // Якщо використовуєш динамічний колір, розкоментуй:
                // bundle.putInt("courseBg", Color.parseColor(course.getColor()));
                bundle.putString("courseImageFilePath", imageFile != null ? imageFile.getAbsolutePath() : null);
                bundle.putString("courseTitle", course.getTitle());
                bundle.putString("coursePrice", course.getPrice());
                bundle.putString("coursePepper", course.getPepper());

                Navigation.findNavController(view).navigate(R.id.nav_course_detail, bundle);
            });

            // 5. Клік по кнопці "Швидке додавання в кошик" (+)
            holder.btnAddToCartSmall.setOnClickListener(v -> {
                int currentQty = Order.itemsMap.getOrDefault(course.getId(), 0);
                Order.itemsMap.put(course.getId(), currentQty + 1);

                Toast.makeText(context, "Додано: " + course.getTitle(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    // Метод для збереження картинки у файл (щоб передати шлях у фрагмент деталей)
    private File saveImageToFile(Bitmap bitmap, String fileName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, fileName);
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return imageFile;
        } catch (IOException e) {
            Log.e("CourseAdapter", "Error saving image: " + e.getMessage());
            return null;
        }
    }

    public static final class CourseViewHolder extends RecyclerView.ViewHolder {
        CardView courseBg;
        ImageView courseImage;
        TextView courseTitle, coursePrice, coursePepper;
        View btnAddToCartSmall; // Кнопка додавання (CardView або ImageView)

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            courseBg = itemView.findViewById(R.id.courseBg);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            coursePepper = itemView.findViewById(R.id.coursePepper);

            // Ця кнопка була додана в новому course_item.xml
            btnAddToCartSmall = itemView.findViewById(R.id.btnAddToCartSmall);
        }
    }
}