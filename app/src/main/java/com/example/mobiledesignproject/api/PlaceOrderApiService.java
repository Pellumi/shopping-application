package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.OrderRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlaceOrderApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/order/place-order/{userId}")
    Call<Void> placeOrder(@Path("userId") String userId, @Body OrderRequest orderRequest);
}
