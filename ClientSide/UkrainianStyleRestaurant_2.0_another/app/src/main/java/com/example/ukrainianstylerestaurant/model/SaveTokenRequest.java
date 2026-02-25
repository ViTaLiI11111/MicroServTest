package com.example.ukrainianstylerestaurant.model;

public class SaveTokenRequest {
    public String username;
    public String role;
    public String token;

    public SaveTokenRequest(String username, String role, String token) {
        this.username = username;
        this.role = role;
        this.token = token;
    }
}