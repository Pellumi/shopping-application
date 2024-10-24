package com.example.mobiledesignproject.model;

public class Product {
    private String productId;
    private String name;
    private String brand;
    private String description;
    private String imageUrl;
    private String price;
    private String quantity;

    public Product(String productId, String name, String brand, String description, String imageUrl, String price, String quantity) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }
    public String getName() {
        return name;
    }
    public String getBrand() {
        return brand;
    }
    public String getDescription() {
        return description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getPrice() {
        return price;
    }
    public String getQuantity() {
        return quantity;
    }
}
