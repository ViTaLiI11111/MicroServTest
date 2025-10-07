package com.example.ukrainianstylerestaurant;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class LocalStorage {

    private static final String PREFS_NAME = "ukrainianstylerestaurant";
    private static final String KEY_COURSES = "courses";
    private static final String KEY_CATEGORIES = "categories";

    public static void saveCourses(Context context, List<Course> courses) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(courses);
        editor.putString(KEY_COURSES, json);
        editor.apply();
    }

    public static List<Course> loadCourses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_COURSES, null);
        Type type = new TypeToken<List<Course>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveCategories(Context context, List<Category> categories) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        editor.putString(KEY_CATEGORIES, json);
        editor.apply();
    }

    public static List<Category> loadCategories(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_CATEGORIES, null);
        Type type = new TypeToken<List<Category>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
