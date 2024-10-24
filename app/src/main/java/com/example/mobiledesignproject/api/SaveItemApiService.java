package com.example.mobiledesignproject.api;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SaveItemApiService {
    @POST("/api/cart/save-item/{userId}/{productId}")
    Call<Void> saveItem(@Path("userId") String userId, @Path("productId") String productId);
}
