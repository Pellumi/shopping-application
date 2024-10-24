package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.Product;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApiService {
    @GET("/api/products/search")
    Call<Map<String, Product>> searchByCategory(@Query("keyword") String keyword);
}
