package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.Product;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApiService {
    @GET("/api/products/get-product/{categoryId}/{subcategoryId}")
    Call<Map<String, Product>> getProducts(
            @Path("categoryId") String categoryId,
            @Path("subcategoryId") String subcategoryId
    );
}
