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

    // --- Ключі для Кешування Меню ---
    private static final String KEY_COURSES = "courses";
    private static final String KEY_CATEGORIES = "categories";

    // --- Ключі для Сесії (Auth) ---
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    // --- Ключі для Профілю Клієнта (Постійні дані) ---
    private static final String KEY_CLIENT_NAME = "client_name";
    private static final String KEY_CLIENT_PHONE = "client_phone";
    private static final String KEY_CLIENT_EMAIL = "client_email"; // Додано Email
    private static final String KEY_CLIENT_ADDRESS = "client_address";

    // --- Ключі для Поточного Замовлення (Транзакційні дані) ---
    private static final String KEY_TABLE_NO = "table_no";       // Якщо в закладі
    private static final String KEY_IS_DELIVERY = "is_delivery"; // Якщо доставка
    private static final String KEY_DELIVERY_ADDR_TEMP = "del_addr_temp"; // Адреса конкретно цього замовлення
    private static final String KEY_DELIVERY_PHONE_TEMP = "del_phone_temp"; // Телефон конкретно цього замовлення

    // --- Ключ для Відстеження ---
    private static final String KEY_ACTIVE_ORDER_ID = "active_order_id";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ==========================================
    // 1. МЕТОДИ ПРОФІЛЮ (ProfileFragment)
    // ==========================================

    public static void saveProfile(Context context, String name, String phone, String email, String address) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_CLIENT_NAME, name);
        editor.putString(KEY_CLIENT_PHONE, phone);
        editor.putString(KEY_CLIENT_EMAIL, email);
        editor.putString(KEY_CLIENT_ADDRESS, address);
        editor.apply();
    }

    public static String getClientName(Context context) {
        return getPrefs(context).getString(KEY_CLIENT_NAME, "");
    }

    public static String getClientPhone(Context context) {
        return getPrefs(context).getString(KEY_CLIENT_PHONE, "");
    }

    public static String getClientEmail(Context context) {
        return getPrefs(context).getString(KEY_CLIENT_EMAIL, "");
    }

    public static String getClientAddress(Context context) {
        return getPrefs(context).getString(KEY_CLIENT_ADDRESS, "");
    }

    // ==========================================
    // 2. МЕТОДИ СЕСІЇ (Login/Logout)
    // ==========================================

    public static void saveLoginSession(Context context, int userId, String username) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static String getUsername(Context context) {
        return getPrefs(context).getString(KEY_USERNAME, "Client");
    }

    public static int getUserId(Context context) {
        return getPrefs(context).getInt(KEY_USER_ID, -1);
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        // Видаляємо дані сесії та профілю, але можна залишити кеш меню
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);

        editor.remove(KEY_CLIENT_NAME);
        editor.remove(KEY_CLIENT_PHONE);
        editor.remove(KEY_CLIENT_EMAIL);
        editor.remove(KEY_CLIENT_ADDRESS);

        editor.remove(KEY_ACTIVE_ORDER_ID);
        editor.apply();
    }

    // ==========================================
    // 3. МЕТОДИ ОФОРМЛЕННЯ ЗАМОВЛЕННЯ
    // ==========================================

    /**
     * Встановлює режим доставки та зберігає дані для поточного замовлення.
     */
    public static void setDeliveryMode(Context context, boolean isDelivery, String address, String phone) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_IS_DELIVERY, isDelivery);

        if (isDelivery) {
            editor.putString(KEY_DELIVERY_ADDR_TEMP, address);
            editor.putString(KEY_DELIVERY_PHONE_TEMP, phone);
            editor.remove(KEY_TABLE_NO); // Якщо доставка, то столик не актуальний
        } else {
            editor.remove(KEY_DELIVERY_ADDR_TEMP);
            editor.remove(KEY_DELIVERY_PHONE_TEMP);
        }
        editor.apply();
    }

    public static boolean isDeliveryMode(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_DELIVERY, false);
    }

    public static String getTempDeliveryAddress(Context context) {
        return getPrefs(context).getString(KEY_DELIVERY_ADDR_TEMP, "");
    }

    public static String getTempDeliveryPhone(Context context) {
        return getPrefs(context).getString(KEY_DELIVERY_PHONE_TEMP, "");
    }

    public static void saveTableNumber(Context context, int tableNo) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(KEY_TABLE_NO, tableNo);
        // Якщо вибрали столик, значить це не доставка
        editor.putBoolean(KEY_IS_DELIVERY, false);
        editor.apply();
    }

    public static int getTableNumber(Context context) {
        return getPrefs(context).getInt(KEY_TABLE_NO, 1);
    }

    // ==========================================
    // 4. МЕТОДИ ВІДСТЕЖЕННЯ (Tracking)
    // ==========================================

    public static void saveActiveOrderId(Context context, String orderId) {
        getPrefs(context).edit().putString(KEY_ACTIVE_ORDER_ID, orderId).apply();
    }

    public static String getActiveOrderId(Context context) {
        return getPrefs(context).getString(KEY_ACTIVE_ORDER_ID, null);
    }

    public static void clearActiveOrder(Context context) {
        getPrefs(context).edit().remove(KEY_ACTIVE_ORDER_ID).apply();
    }

    // ==========================================
    // 5. МЕТОДИ КЕШУВАННЯ МЕНЮ (Старі, без змін)
    // ==========================================

    public static void saveCourses(Context context, List<Course> courses) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(courses);
        editor.putString(KEY_COURSES, json);
        editor.apply();
    }

    public static List<Course> loadCourses(Context context) {
        SharedPreferences prefs = getPrefs(context);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_COURSES, null);
        Type type = new TypeToken<List<Course>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveCategories(Context context, List<Category> categories) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        editor.putString(KEY_CATEGORIES, json);
        editor.apply();
    }

    public static List<Category> loadCategories(Context context) {
        SharedPreferences prefs = getPrefs(context);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_CATEGORIES, null);
        Type type = new TypeToken<List<Category>>() {}.getType();
        return gson.fromJson(json, type);
    }
}