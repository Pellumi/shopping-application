package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.SavedItemsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetSavedItemApiClass {
    @GET("/api/cart/get-saved-item/{userId}")
    Call<SavedItemsResponse> getSavedItems(@Path("userId") String userId);
}
