package com.example.ukrainianstylerestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ukrainianstylerestaurant.model.Order;

import java.io.File;

public class CoursePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);

        ConstraintLayout courseBg = findViewById(R.id.coursePageBg);
        ImageView courseImage = findViewById(R.id.coursePageImage);

        // Отримання шляху до файлу зображення з інтенції
        String imageFilePath = getIntent().getStringExtra("courseImageFilePath");
        if (imageFilePath != null && !imageFilePath.isEmpty()) {
            File imageFile = new File(imageFilePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                courseImage.setImageBitmap(bitmap);
            } else {
                courseImage.setImageResource(R.drawable.default_image); // default image in case file not found
            }
        } else {
            courseImage.setImageResource(R.drawable.default_image); // default image
        }

        TextView courseTitle = findViewById(R.id.coursePageTitle);
        TextView coursePrice = findViewById(R.id.coursePagePrice);
        TextView coursePepper = findViewById(R.id.coursePagePepper);

        courseBg.setBackgroundColor(getIntent().getIntExtra("courseBg", 0));
        courseTitle.setText(getIntent().getStringExtra("courseTitle"));
        coursePrice.setText(getIntent().getStringExtra("coursePrice"));
        coursePepper.setText(getIntent().getStringExtra("coursePepper"));
    }

    public void aboutUs(View view){
        Intent intent = new Intent(this, AboutUs.class);
        startActivity(intent);
    }

    public void goToContacts(View view){
        Intent intent = new Intent(this, Contacts.class);
        startActivity(intent);
    }

    public void mainPage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addToCart(View view){
        int item_id = getIntent().getIntExtra("courseId", 0);
        Order.items_id.add(item_id);
        Toast.makeText(this,"Додано!", Toast.LENGTH_LONG).show();
    }
}
