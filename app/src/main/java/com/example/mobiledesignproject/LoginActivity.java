package com.example.mobiledesignproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobiledesignproject.api.AuthApiService;
import com.example.mobiledesignproject.api.CreateTokenApiService;
import com.example.mobiledesignproject.model.FcmToken;
import com.example.mobiledesignproject.model.LoginRequest;
import com.example.mobiledesignproject.model.LoginResponse;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText matricNo, mPassword;
    TextView togglePassword;
    Button mLogin;
    private AuthApiService authApiService;
    Intent intent;
    Context context = LoginActivity.this;
    private final UIMethods ui = new UIMethods(context, intent);

    Retrofit retrofit = RetrofitClient.getClient();
    CreateTokenApiService createTokenApiService = retrofit.create(CreateTokenApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColor));

        matricNo = findViewById(R.id.matric_no);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login_btn);
        togglePassword = findViewById(R.id.toggle_password);
        AtomicBoolean isPasswordVisible = new AtomicBoolean(false);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        ui.changeUI(matricNo);
        ui.changeUI(mPassword);

        togglePassword.setOnClickListener(v -> {
            isPasswordVisible.set(!isPasswordVisible.get());
            ui.togglePassword(mPassword, togglePassword,isPasswordVisible.get());
        });

        mLogin.setOnClickListener(v -> {
            String matric_num = ui.replaceSlashWithHyphen(matricNo.getText().toString().trim());
            String pass = mPassword.getText().toString().trim();

            loginUser(matric_num, pass);
        });
    }

    private void loginUser(String matric_no, String password){
        if(matric_no.isEmpty() || password.isEmpty()){
            ui.showLowerRegularSnackBar(context, "Please enter your matric number and password", R.color.danger_color, R.id.main);
            return;
        }

        mLogin.setText(R.string.please_wait); // TODO: work on this text change for the button

        matricNo.setEnabled(false);
        matricNo.setAlpha(0.5F);
        mPassword.setEnabled(false);
        mPassword.setAlpha(0.5F);

        Log.e("LoginActivity", "Login started");
        LoginRequest loginRequest = new LoginRequest(matric_no, password);

        Call<LoginResponse> call = authApiService.loginUser(loginRequest);
        Log.e("LoginActivity", "Request made");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()){
                    mLogin.setText(R.string.login);
                    LoginResponse loginResponse = response.body();
                    Gson gson = new Gson();
                    String loginResponseJson = gson.toJson(loginResponse);

                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loginResponse", loginResponseJson);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    createNewTokenAfterLogin(matric_no);

                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mLogin.setText(R.string.login);
                    matricNo.setEnabled(true);
                    matricNo.setAlpha(1F);
                    mPassword.setEnabled(true);
                    mPassword.setAlpha(1F);
                    if (response.code() == 404){
                        ui.showLowerRegularSnackBar(context, "User not found", R.color.danger_color, R.id.main);
                    } else if (response.code() == 401) {
                        ui.showLowerRegularSnackBar(context, "Username or password is incorrect", R.color.danger_color, R.id.main);
                    } else {
                        ui.showLowerRegularSnackBar(context, "Login failed, please try again later", R.color.danger_color, R.id.main);
                        Log.e("LoginError", "Other Error: " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                mLogin.setText(R.string.login);
                matricNo.setEnabled(true);
                matricNo.setAlpha(1F);
                mPassword.setEnabled(true);
                mPassword.setAlpha(1F);
                ui.showLowerRegularSnackBar(context, "Error: " + t.getMessage(), R.color.danger_color, R.id.main);
                Log.e("LoginError", "Request failed: " + t.getMessage());
            }
        });
    }

    public void createNewTokenAfterLogin(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();

                    Log.d(TAG, "FCM Token: " + token);
                    sendTokenToServer(userId, token);
                });
    }

    private void sendTokenToServer(String userId, String token){
        FcmToken fcmToken = new FcmToken(token);
        Call<Void> call = createTokenApiService.createToken(userId, fcmToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("LoginActivity", "Token sent successfully");
                } else {
                    Log.e("LoginActivity", "Failed to send token, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("LoginActivity", "Error sending token", t);
            }
        });
    }
}