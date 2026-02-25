package com.example.ukrainianstylerestaurant.ui.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.adapter.CategoryAdapter;
import com.example.ukrainianstylerestaurant.adapter.CourseAdapter;
import com.example.ukrainianstylerestaurant.data.MenuRepository;
import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecycler, courseRecycler;
    private CategoryAdapter categoryAdapter;
    private CourseAdapter courseAdapter;

    List<Course> courseList = new ArrayList<>();
    public static List<Course> fullCoursesList = new ArrayList<>();

    List<Category> categoryList = new ArrayList<>();

    private ExecutorService executorService;
    private MenuRepository repo;
    private Handler mainThreadHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repo = new MenuRepository();
        executorService = Executors.newFixedThreadPool(2);
        mainThreadHandler = new Handler(Looper.getMainLooper());

        setCategoryRecycler(categoryList, view);
        setCourseRecycler(courseList, view);

        ImageButton returnAllBtn = view.findViewById(R.id.return_first_list);
        returnAllBtn.setOnClickListener(v -> returnAllCourses());


        if (!isInternetAvailable()) {
            Toast.makeText(getContext(), "Немає з'єднання з Інтернетом", Toast.LENGTH_SHORT).show();

            List<Category> savedCategories = LocalStorage.loadCategories(requireContext());
            if (savedCategories != null) {
                categoryList.addAll(savedCategories);
                categoryAdapter.notifyDataSetChanged();
            }

            List<Course> savedCourses = LocalStorage.loadCourses(requireContext());
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

    private void fetchCategoriesFromApi() {
        try {
            List<Category> cats = repo.getCategories();
            categoryList.clear();
            if (cats != null) categoryList.addAll(cats);
            LocalStorage.saveCategories(requireContext(), categoryList);
            mainThreadHandler.post(() -> categoryAdapter.notifyDataSetChanged());
        } catch (Exception ex) {
            Log.e("HomeFragment", "getCategories error", ex);
            mainThreadHandler.post(() ->
                    Toast.makeText(requireContext(), "Помилка завантаження категорій", Toast.LENGTH_SHORT).show());
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
            LocalStorage.saveCourses(requireContext(), fullCoursesList);
            mainThreadHandler.post(() -> courseAdapter.notifyDataSetChanged());
        } catch (Exception ex) {
            Log.e("HomeFragment", "getDishes error", ex);
            mainThreadHandler.post(() ->
                    Toast.makeText(requireContext(), "Помилка завантаження страв", Toast.LENGTH_SHORT).show());
        }
    }

    private boolean isInternetAvailable() {
        Context context = requireContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private void setCategoryRecycler(List<Category> list, View view) {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        categoryRecycler = view.findViewById(R.id.categoryRecycler);
        categoryRecycler.setLayoutManager(lm);
        categoryAdapter = new CategoryAdapter(requireContext(), list, this);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private void setCourseRecycler(List<Course> list, View view) {
        RecyclerView.LayoutManager lm = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        courseRecycler = view.findViewById(R.id.courseRecycler);
        courseRecycler.setLayoutManager(lm);
        courseAdapter = new CourseAdapter(requireContext(), list);
        courseRecycler.setAdapter(courseAdapter);
    }


    public void returnAllCourses() {
        courseList.clear();
        courseList.addAll(fullCoursesList);
        courseAdapter.notifyDataSetChanged();
    }

    public void showCoursesByCategory(int categoryId) {
        courseList.clear();
        courseList.addAll(fullCoursesList);

        List<Course> filtered = new ArrayList<>();
        for (Course c : courseList) {
            // Логіка фільтрації
            int cat = (c.getCategoryId() != 0) ? c.getCategoryId() : c.getCategoryId();
            if (cat == categoryId) filtered.add(c);
        }
        courseList.clear();
        courseList.addAll(filtered);
        courseAdapter.notifyDataSetChanged();
    }
}