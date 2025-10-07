package com.example.ukrainianstylerestaurant.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.CoursePage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Course;

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
        View courseItems = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseAdapter.CourseViewHolder(courseItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(holder.getAdapterPosition());
        if (course != null) {
            try {
                holder.courseBg.setCardBackgroundColor(Color.parseColor(course.getColor()));
            } catch (IllegalArgumentException e) {
                holder.courseBg.setCardBackgroundColor(Color.GRAY); // default color in case of error
                Log.e("CourseAdapter", "Invalid color format: " + course.getColor());
            }

            String imageBase64 = course.getImg();
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.courseImage.setImageBitmap(decodedByte);

                    // Збереження зображення у файл
                    File imageFile = saveImageToFile(decodedByte, "course_" + course.getId() + ".png");
                    if (imageFile != null) {
                        holder.itemView.setOnClickListener(view -> {
                            Intent intent = new Intent(context, CoursePage.class);
                            intent.putExtra("courseBg", Color.parseColor(course.getColor()));
                            intent.putExtra("courseImageFilePath", imageFile.getAbsolutePath()); // передаємо шлях до файлу зображення
                            intent.putExtra("courseTitle", course.getTitle());
                            intent.putExtra("coursePrice", course.getPrice());
                            intent.putExtra("coursePepper", course.getPepper());
                            intent.putExtra("courseId", course.getId());
                            context.startActivity(intent);
                        });
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("CourseAdapter", "Base64 decoding error: " + e.getMessage());
                    holder.courseImage.setImageResource(R.drawable.default_image); // default image in case of error
                } catch (Exception e) {
                    Log.e("CourseAdapter", "Unexpected error: " + e.getMessage());
                    holder.courseImage.setImageResource(R.drawable.default_image); // default image in case of error
                }
            } else {
                holder.courseImage.setImageResource(R.drawable.default_image); // default image
            }

            holder.courseTitle.setText(course.getTitle());
            holder.coursePrice.setText(course.getPrice());
            holder.coursePepper.setText(course.getPepper());
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

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
            Log.e("CourseAdapter", "Error saving image file: " + e.getMessage());
            return null;
        }
    }

    public static final class CourseViewHolder extends RecyclerView.ViewHolder {
        CardView courseBg;
        ImageView courseImage;
        TextView courseTitle, coursePrice, coursePepper;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            courseBg = itemView.findViewById(R.id.courseBg);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            coursePepper = itemView.findViewById(R.id.coursePepper);
        }
    }
}
