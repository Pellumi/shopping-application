package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.CartResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ShowCartApiService {
    @GET("/api/cart/show-cart/{userId}")
    Call<Map<String, CartResponse>> showCart(@Path("userId") String userId);
}
