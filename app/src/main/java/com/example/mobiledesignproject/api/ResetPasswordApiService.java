package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ResetPasswordApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/users/reset-password/{userId}")
    Call<Void> resetPassword(@Path("userId") String userId, @Body ResetPasswordRequest resetPasswordRequest);
}
