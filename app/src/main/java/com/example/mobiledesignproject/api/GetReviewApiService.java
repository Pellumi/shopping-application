package com.example.mobiledesignproject.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetReviewApiService {
    @GET("/api/products/get-product-review/{productId}")
    Call<Void> getProduct(@Path("productId") String productId);
}
