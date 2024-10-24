package com.example.mobiledesignproject.api;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface RemoveSavedItemApiService {
    @DELETE("/api/cart/remove-saved-item/{userId}/{productId}")
    Call<Void> removedSavedItem(@Path("userId") String userId, @Path("productId") String productId);
}
