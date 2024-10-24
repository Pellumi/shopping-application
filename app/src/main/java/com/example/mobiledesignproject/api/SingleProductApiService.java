package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SingleProductApiService {
    @GET("/api/products/product-id/{productId}")
    Call<Product> getProduct( @Path("productId") String productId );
}
