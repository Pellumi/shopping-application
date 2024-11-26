package com.example.mobiledesignproject.model;

import java.util.Map;

public class SavedItemsResponse {
    private Map<String, SavedItem> savedItems;

    public Map<String, SavedItem> getSavedItems() {
        return savedItems;
    }

    public void setSavedItems(Map<String, SavedItem> savedItems) {
        this.savedItems = savedItems;
    }

    public static class SavedItem {
        private String productId;
        private String name;
        private String brand;
        private String imageUrl;
        private String price;
        private String quantity;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }
}
