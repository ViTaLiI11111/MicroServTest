package com.example.ukrainianstylerestaurant.model;

import java.util.List;

public class CreateOrderRequest {
    public int tableNo;
    public List<OrderItemRequest> items;

    public CreateOrderRequest(int tableNo, List<OrderItemRequest> items) {
        this.tableNo = tableNo;
        this.items = items;
    }
}
