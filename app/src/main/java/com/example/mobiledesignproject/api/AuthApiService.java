package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.LoginRequest;
import com.example.mobiledesignproject.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
