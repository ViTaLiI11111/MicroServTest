package com.example.ukrainianstylerestaurant.model;

public class OrderItemRequest {
    public int dishId;
    public int qty;
    public String notes;

    public OrderItemRequest(int dishId, int qty, String notes) {
        this.dishId = dishId;
        this.qty = qty;
        this.notes = notes;
    }
}
