package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.OrderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetOrderApiService {
    @GET("/api/order/get-order/{userId}")
    Call<OrderResponse> getOrders(@Path("userId") String userId);
}
