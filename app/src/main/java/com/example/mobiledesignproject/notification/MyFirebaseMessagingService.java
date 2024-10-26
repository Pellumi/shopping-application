package com.example.mobiledesignproject.notification;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mobiledesignproject.R;
import com.example.mobiledesignproject.api.UpdateTokenApiService;
import com.example.mobiledesignproject.model.FcmToken;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferences shardPreferences;

    Retrofit retrofit = RetrofitClient.getClient();
    UpdateTokenApiService updateTokenApiService = retrofit.create(UpdateTokenApiService.class);

    int NOTIFICATIONS_PERMISSION_REQUEST_CODE = 101;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        getFirebaseMessage(title, message);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FCM", "New token: " + token);

        shardPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String loginResponseJson = shardPreferences.getString("loginResponse", "");


        if(!loginResponseJson.isEmpty()) {
            Gson gson = new Gson();
            LoginResponse loginResponse = gson.fromJson(loginResponseJson, LoginResponse.class);

            String userId = loginResponse.getUserDetails().getUserId();
            sendTokenToServer(token, userId);
        }
    }

    private void sendTokenToServer(String token, String userId) {
        FcmToken fcmToken = new FcmToken(token);
        Call<Void> call = updateTokenApiService.updateToken(userId, fcmToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@androidx.annotation.NonNull Call<Void> call, @androidx.annotation.NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MyFirebaseMessagingService", "Token updated successfully");
                } else {
                    Log.e("MyFirebaseMessagingService", "Failed to update token, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@androidx.annotation.NonNull Call<Void> call, @androidx.annotation.NonNull Throwable t) {
                Log.e("LoginActivity", "Error updating token", t);
            }
        });
    }

    private void getFirebaseMessage(String title, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myFirebaseChannel")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Set up the method to handle notificaton permission
            return;
        }
        manager.notify(101, builder.build());
    }
}
