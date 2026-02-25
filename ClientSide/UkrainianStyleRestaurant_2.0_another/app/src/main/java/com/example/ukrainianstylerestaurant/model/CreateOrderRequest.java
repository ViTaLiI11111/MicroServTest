package com.example.ukrainianstylerestaurant.model;

import java.util.List;

public class CreateOrderRequest {
    public int tableNo;
    public List<OrderItemRequest> items;
    public int type;
    public String address;
    public String phone;
    public String clientName;

    public CreateOrderRequest(int tableNo, List<OrderItemRequest> items, String clientName) {
        this.tableNo = tableNo;
        this.items = items;
        this.type = 0;
        this.clientName = clientName;
        this.address = null;
        this.phone = null;
    }

    public CreateOrderRequest(List<OrderItemRequest> items, String address, String phone, String clientName) {
        this.tableNo = 0;
        this.items = items;
        this.type = 1;
        this.address = address;
        this.phone = phone;
        this.clientName = clientName;
    }
}