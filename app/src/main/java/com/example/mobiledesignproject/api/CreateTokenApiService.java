package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.FcmToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CreateTokenApiService {
    @POST("/api/users/create-token/{userId}")
    Call<Void> createToken(@Path("userId") String userId, @Body FcmToken fcmToken);
}
