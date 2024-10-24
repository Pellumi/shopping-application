package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.Product;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchByNameApiService {
    @GET("/api/products/search-by-name")
    Call<Map<String, Product>> searchByName(@Query("keyword") String keyword);
}
