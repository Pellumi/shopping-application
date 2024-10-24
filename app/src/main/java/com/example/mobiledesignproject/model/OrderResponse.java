package com.example.mobiledesignproject.model;

import java.util.Map;

public class OrderResponse {
    private String message;
    private Map<String, Order> orders;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Order> getOrders() {
        return orders;
    }

    public void setOrders(Map<String, Order> orders) {
        this.orders = orders;
    }
}
