package com.example.ukrainianstylerestaurant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.adapter.CategoryAdapter;
import com.example.ukrainianstylerestaurant.adapter.CourseAdapter;
import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    RecyclerView categoryRecycler, courseRecycler;
    CategoryAdapter categoryAdapter;
    CourseAdapter courseAdapter;
    List<Course> courseList = new ArrayList<>();
    static List<Course> fullCoursesList = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();
    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newFixedThreadPool(2); // Паралельне завантаження

        setCategoryRecycler(categoryList);
        setCourseRecycler(courseList);

        if (!isInternetAvailable()) {
            Toast.makeText(this, "Немає з'єднання з Інтернетом", Toast.LENGTH_SHORT).show();
            // Завантаження даних з локального сховища
            List<Category> savedCategories = LocalStorage.loadCategories(this);
            if (savedCategories != null) {
                categoryList.addAll(savedCategories);
                categoryAdapter.notifyDataSetChanged();
            }

            List<Course> savedCourses = LocalStorage.loadCourses(this);
            if (savedCourses != null) {
                courseList.addAll(savedCourses);
                fullCoursesList.addAll(savedCourses);
                courseAdapter.notifyDataSetChanged();
            }
        } else {
            executorService.execute(this::fetchCategoriesFromFirebase);
            executorService.execute(this::fetchCoursesFromFirebase);
        }
    }

    private void fetchCategoriesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReference.keepSynced(true); // Використання кешування
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                // Збереження даних в локальне сховище
                LocalStorage.saveCategories(MainActivity.this, categoryList);
                runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read data from Firebase", databaseError.toException());
            }
        });
    }

    private void fetchCoursesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Dishes");
        databaseReference.keepSynced(true); // Використання кешування
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseList.clear();
                fullCoursesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Course course = snapshot.getValue(Course.class);
                    if (course != null) {
                        courseList.add(course);
                        fullCoursesList.add(course);
                    }
                }
                // Збереження даних в локальне сховище
                LocalStorage.saveCourses(MainActivity.this, fullCoursesList);
                runOnUiThread(() -> courseAdapter.notifyDataSetChanged());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read data from Firebase", databaseError.toException());
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void setCategoryRecycler(List<Category> categoryList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        categoryRecycler = findViewById(R.id.categoryRecycler);
        categoryRecycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private void setCourseRecycler(List<Course> courseList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        courseRecycler = findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(layoutManager);
        courseAdapter = new CourseAdapter(this, courseList);
        courseRecycler.setAdapter(courseAdapter);
    }

    public void returnAllCourses(View view) {
        courseList.clear();
        courseList.addAll(fullCoursesList);
        courseAdapter.notifyDataSetChanged();
    }

    public void showCoursesByCategory(int category) {
        courseList.clear();
        courseList.addAll(fullCoursesList);
        List<Course> filterCourses = new ArrayList<>();
        for (Course c : courseList) {
            if (c.getCategory() == category) {
                filterCourses.add(c);
            }
        }
        courseList.clear();
        courseList.addAll(filterCourses);
        courseAdapter.notifyDataSetChanged();
    }

    public void aboutUs(View view) {
        Intent intent = new Intent(this, AboutUs.class);
        startActivity(intent);
    }

    public void goToContacts(View view) {
        Intent intent = new Intent(this, Contacts.class);
        startActivity(intent);
    }

    public void openShoppingCart(View view) {
        Intent intent = new Intent(this, OrderPage.class);
        startActivity(intent);
    }
}
