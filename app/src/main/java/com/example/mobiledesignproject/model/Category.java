package com.example.mobiledesignproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Category {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("subcategories")
    private List<SubCategory> subCategories;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }
}
