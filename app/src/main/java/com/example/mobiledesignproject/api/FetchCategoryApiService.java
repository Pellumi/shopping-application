package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FetchCategoryApiService {
    @GET("/api/products/show-category")
    Call<List<Category>> fetchCategory();
}
