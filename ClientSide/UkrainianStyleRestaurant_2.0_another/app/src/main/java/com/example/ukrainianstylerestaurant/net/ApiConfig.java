package com.example.ukrainianstylerestaurant.net;

public final class ApiConfig {

    // СЮДИ ВСТАВ СВІЙ ДОМЕН З NGROK (Обов'язково з / в кінці)
    // Приклад: "https://unsuburbed-omar-dioptrically.ngrok-free.dev/"
    public static final String BASE_URL = "https://unsuburbed-omar-dioptrically.ngrok-free.dev/";

    // Тепер всі сервіси доступні за однією адресою завдяки Nginx
    public static final String ORDERS_BASE_URL = BASE_URL;
    public static final String AUTH_BASE_URL = BASE_URL;

    private ApiConfig() {}
}