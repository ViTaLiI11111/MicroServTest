package com.example.ukrainianstylerestaurant.model;

public class Course {

    int id, category;
    String img, title, price, pepper, color;

    public Course() {}

    public Course(int id, String img, String title, String price, String pepper, String color, int category) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.price = price;
        this.pepper = pepper;
        this.color = color;
        this.category = category;
    }


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPepper() {
        return pepper;
    }

    public void setPepper(String pepper) {
        this.pepper = pepper;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
