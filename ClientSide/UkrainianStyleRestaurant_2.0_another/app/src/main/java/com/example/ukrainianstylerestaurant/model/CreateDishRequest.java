package com.example.ukrainianstylerestaurant.model;

public class CreateDishRequest {
    public String title;
    public double price;
    public String pepper;
    public String color;
    public int categoryId;
    public String imageBase64;

    public CreateDishRequest(String title, double price, String pepper,
                             String color, int categoryId, String imageBase64) {
        this.title = title;
        this.price = price;
        this.pepper = pepper;
        this.color = color;
        this.categoryId = categoryId;
        this.imageBase64 = imageBase64;
    }
}

