package com.example.mobiledesignproject.model;

public class CartResponse {
    private String name;
    private String productId;
    private int quantity;
    private int price;
    private int totalPrice;
    private String imageUrl;
    private int itemNumber;

    public CartResponse(String productId, int quantity, int price, int totalPrice){
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public int getTotalPrice() { return totalPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getItemNumber() {
        return itemNumber;
    }
    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }
}
