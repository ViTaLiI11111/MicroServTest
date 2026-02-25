package com.example.ukrainianstylerestaurant.model;

import java.util.List;

public class OrderResponse {
    public String id;
    public int tableNo;
    public String status;
    public String type;
    public double total;
    public String createdAt;
    public String deliveryAddress;
    public String clientPhone;
    public String clientName;

    public boolean isPaid;
    public String paidAt;

    public List<OrderItemResponse> items;
}