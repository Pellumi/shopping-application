package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.ReviewRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostReviewApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/products/add-product-review/{productId}")
    Call<Void> postReview(@Path("productId") String productId, @Body ReviewRequest reviewRequest);
}
