package com.example.mobiledesignproject.model;

import com.google.gson.annotations.SerializedName;

public class SubCategory {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
