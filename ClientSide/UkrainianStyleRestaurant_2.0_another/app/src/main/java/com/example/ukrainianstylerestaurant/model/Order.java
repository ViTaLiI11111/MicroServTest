package com.example.ukrainianstylerestaurant.model;

import java.util.HashMap;
import java.util.Map;

public class Order {
    // Змінюємо Set на Map: Key = DishID, Value = Quantity
    public static Map<Integer, Integer> itemsMap = new HashMap<>();
}