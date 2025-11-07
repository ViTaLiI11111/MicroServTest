package com.example.ukrainianstylerestaurant.model;

public class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public String phone;

    public RegisterRequest(String username, String password, String email, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }
}