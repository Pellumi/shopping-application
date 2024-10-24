package com.example.mobiledesignproject.model;

public class OrderRequest {
    private String totalAmount;
    private String address;

    public OrderRequest(String totalAmount, String address){
        this.totalAmount = totalAmount;
        this.address = address;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
