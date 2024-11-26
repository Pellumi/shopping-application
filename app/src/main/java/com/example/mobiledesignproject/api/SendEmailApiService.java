package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.RecoveryEmail;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SendEmailApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/users/password-recovery/{userId}")
    Call<Void> sendRecoveryEmail(@Path("userId") String userId, @Body RecoveryEmail recoveryEmail);
}
