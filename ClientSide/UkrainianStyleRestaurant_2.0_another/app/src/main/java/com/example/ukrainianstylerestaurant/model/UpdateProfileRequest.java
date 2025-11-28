package com.example.ukrainianstylerestaurant.model;

public class UpdateProfileRequest {
    public int id;
    public String fullName; // <--- Має збігатися з C# (FullName -> fullName)
    public String email;
    public String phone;

    public UpdateProfileRequest(int id, String fullName, String email, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
}