package com.example.ukrainianstylerestaurant.model;

public class OrderResponse {
    public String id;        // Це наш GUID (ID замовлення)
    public int tableNo;
    public String status;
    public double total;
    public String createdAt;
}