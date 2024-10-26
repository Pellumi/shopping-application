package com.example.mobiledesignproject.api;

import com.example.mobiledesignproject.model.FcmToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UpdateTokenApiService {
    @PUT("/api/users/update-token/{userId}")
    Call<Void> updateToken(@Path("userId") String userId, @Body FcmToken fcmToken);
}
