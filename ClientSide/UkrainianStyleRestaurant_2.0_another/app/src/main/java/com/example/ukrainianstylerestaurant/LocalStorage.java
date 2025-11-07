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

    // --- ПОЧАТОК НОВИХ ПОЛІВ ---
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TABLE_NO = "table_no"; // Для збереження номера столика

    /**
     * Отримує екземпляр SharedPreferences
     */
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Зберігає сесію юзера після логіну
     */
    public static void saveLoginSession(Context context, int userId, String username) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * Очищує сесію юзера
     */
    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_TABLE_NO); // Також очищуємо столик
        editor.apply();
    }

    /**
     * Перевіряє, чи юзер залогінений
     */
    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Зберігає номер столика, який обрав клієнт
     */
    public static void saveTableNumber(Context context, int tableNo) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(KEY_TABLE_NO, tableNo);
        editor.apply();
    }

    /**
     * Отримує збережений номер столика
     */
    public static int getTableNumber(Context context) {
        return getPrefs(context).getInt(KEY_TABLE_NO, 1); // Повертаємо 1 за замовчуванням
    }

    /**
     * Отримує ім'я залогіненого юзера
     */
    public static String getUsername(Context context) {
        return getPrefs(context).getString(KEY_USERNAME, "Client");
    }

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
