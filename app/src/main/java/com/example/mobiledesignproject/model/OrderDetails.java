package com.example.mobiledesignproject.model;

import java.util.Map;

public class OrderDetails {
    private String address;
    private Map<String, Item> items;
    private String order_date;
    private String status;
    private String totalAmount;
    private String updated_at;

    public String getAddress() {
        return address;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public String getOrderDate() {
        return order_date;
    }

    public String getStatus() {
        return status;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getUpdatedAt() {
        return updated_at;
    }
}
