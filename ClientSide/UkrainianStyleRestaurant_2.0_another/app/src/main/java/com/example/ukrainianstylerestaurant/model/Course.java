package com.example.ukrainianstylerestaurant.model;

import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("id")
    private int id;

    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("imageBase64")
    private String imageBase64;

    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private String price;

    @SerializedName("pepper")
    private String pepper;

    @SerializedName("color")
    private String color;

    public Course() {}

    public Course(int id, String imageBase64, String title, String price,
                  String pepper, String color, int categoryId) {
        this.id = id;
        this.imageBase64 = imageBase64;
        this.title = title;
        this.price = price;
        this.pepper = pepper;
        this.color = color;
        this.categoryId = categoryId;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getPepper() { return pepper; }
    public void setPepper(String pepper) { this.pepper = pepper; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
