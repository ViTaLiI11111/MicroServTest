package com.example.ukrainianstylerestaurant.model;

public class UpdateProfileRequest {
    public int id;
    public String fullName;
    public String email;
    public String phone;
    public String address;

    public UpdateProfileRequest(int id, String fullName, String email, String phone, String address) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}