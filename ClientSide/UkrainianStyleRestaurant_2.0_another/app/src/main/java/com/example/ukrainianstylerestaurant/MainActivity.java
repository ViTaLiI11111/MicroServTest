package com.example.ukrainianstylerestaurant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.adapter.CategoryAdapter;
import com.example.ukrainianstylerestaurant.adapter.CourseAdapter;
import com.example.ukrainianstylerestaurant.data.MenuRepository;
import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    RecyclerView categoryRecycler, courseRecycler;
    CategoryAdapter categoryAdapter;
    CourseAdapter courseAdapter;

    List<Course> courseList = new ArrayList<>();
    public static List<Course> fullCoursesList = new ArrayList<>();

    List<Category> categoryList = new ArrayList<>();
    ExecutorService executorService;
    MenuRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repo = new MenuRepository();
        executorService = Executors.newFixedThreadPool(2);

        setCategoryRecycler(categoryList);
        setCourseRecycler(courseList);

        if (!isInternetAvailable()) {
            Toast.makeText(this, "Немає з'єднання з Інтернетом", Toast.LENGTH_SHORT).show();

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
            executorService.execute(this::fetchCategoriesFromApi);
            executorService.execute(this::fetchCoursesFromApi);
        }
    }

    // ---------- API loaders ----------
    private void fetchCategoriesFromApi() {
        try {
            List<Category> cats = repo.getCategories();
            categoryList.clear();
            if (cats != null) categoryList.addAll(cats);
            LocalStorage.saveCategories(this, categoryList);
            runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
        } catch (Exception ex) {
            Log.e("MainActivity", "getCategories error", ex);
            runOnUiThread(() ->
                    Toast.makeText(this, "Помилка завантаження категорій", Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchCoursesFromApi() {
        try {
            List<Course> dishes = repo.getDishes();
            courseList.clear();
            fullCoursesList.clear();
            if (dishes != null) {
                courseList.addAll(dishes);
                fullCoursesList.addAll(dishes);
            }
            LocalStorage.saveCourses(this, fullCoursesList);
            runOnUiThread(() -> courseAdapter.notifyDataSetChanged());
        } catch (Exception ex) {
            Log.e("MainActivity", "getDishes error", ex);
            runOnUiThread(() ->
                    Toast.makeText(this, "Помилка завантаження страв", Toast.LENGTH_SHORT).show());
        }
    }


    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private void setCategoryRecycler(List<Category> list) {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        categoryRecycler = findViewById(R.id.categoryRecycler);
        categoryRecycler.setLayoutManager(lm);
        categoryAdapter = new CategoryAdapter(this, list, this);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private void setCourseRecycler(List<Course> list) {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        courseRecycler = findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(lm);
        courseAdapter = new CourseAdapter(this, list);
        courseRecycler.setAdapter(courseAdapter);
    }

    public void returnAllCourses(View view) {
        courseList.clear();
        courseList.addAll(fullCoursesList);
        courseAdapter.notifyDataSetChanged();
    }

    public void showCoursesByCategory(int categoryId) {
        courseList.clear();
        courseList.addAll(fullCoursesList);

        List<Course> filtered = new ArrayList<>();
        for (Course c : courseList) {

            int cat = (c.getCategoryId() != 0) ? c.getCategoryId() : c.getCategoryId();
            if (cat == categoryId) filtered.add(c);
        }
        courseList.clear();
        courseList.addAll(filtered);
        courseAdapter.notifyDataSetChanged();
    }

    public void aboutUs(View view) {
        startActivity(new Intent(this, AboutUs.class));
    }

    public void goToContacts(View view) {
        startActivity(new Intent(this, Contacts.class));
    }

    public void openShoppingCart(View view) {
        startActivity(new Intent(this, OrderPage.class));
    }

    // --- НОВИЙ МЕТОД ДЛЯ КНОПКИ "ВИЙТИ" ---
    /**
     * Викликається кнопкою "Вийти" (btnLogoutMain) з activity_main.xml
     */
    public void logout(View view) {
        // Очищуємо локальну сесію
        LocalStorage.logout(this);

        // Повертаємось на екран логіну
        Intent intent = new Intent(this, LoginActivity.class);
        // Встановлюємо прапори, щоб очистити історію
        // і користувач не міг натиснути "Назад"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Закриваємо MainActivity
    }
}