package com.example.mobiledesignproject.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetOrderDetailsApiService {
    @GET("/api/order/get-order-details/{orderId}/{userId}")
    Call<Void> getOrderDetails(@Path("orderId") String orderId, @Path("userId") String userId);
}
