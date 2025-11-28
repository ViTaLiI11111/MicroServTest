package com.example.ukrainianstylerestaurant.model;

public class DeliveryStatusResponse {
    public int id;
    public int status; // 0=Created, 1=Assigned, 2=PickedUp, 3=Delivered
    public Integer courierId; // Integer, щоб могло бути null
    public String deliveredAt; // Можна додати, якщо треба
}