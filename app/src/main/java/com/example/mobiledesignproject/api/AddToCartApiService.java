package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.CartItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddToCartApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/cart/add-to-cart/{userId}")
    Call<Void> addToCart(@Path("userId") String userId, @Body CartItem cartItem);
}
