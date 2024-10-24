package com.example.mobiledesignproject.api;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface RemoveFromCartService {
    @DELETE("/api/cart/remove-from-cart/{userId}/{productId}")
    Call<Void> removeFromCart(@Path("userId") String userId, @Path("productId") String productId);
}
