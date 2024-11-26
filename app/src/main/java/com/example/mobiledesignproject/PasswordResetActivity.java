package com.example.mobiledesignproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.mobiledesignproject.api.ResetPasswordApiService;
import com.example.mobiledesignproject.model.ResetPasswordRequest;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PasswordResetActivity extends AppCompatActivity {

    EditText mToken, mNewPassword;
    TextView togglePassword;
    Button mReset;
    Intent intent;
    Context context = PasswordResetActivity.this;
    private final UIMethods ui = new UIMethods();
    Retrofit retrofit = RetrofitClient.getClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColor));

        mToken = findViewById(R.id.matric_no);
        mNewPassword = findViewById(R.id.password);
        mReset = findViewById(R.id.login_btn);
        togglePassword = findViewById(R.id.toggle_password);
        AtomicBoolean isPasswordVisible = new AtomicBoolean(false);

        intent = getIntent();
        String user_id = intent.getStringExtra("user-id");

        ui.changeUI(mToken);
        ui.changeUI(mNewPassword);

        togglePassword.setOnClickListener(v -> {
            isPasswordVisible.set(!isPasswordVisible.get());
            ui.togglePassword(mNewPassword, togglePassword,isPasswordVisible.get());
        });

        mReset.setOnClickListener(v -> {
            String token = mToken.getText().toString().trim();
            String newPassword = mNewPassword.getText().toString().trim();

            resetPassword(user_id, token, newPassword);
        });
    }

    private void resetPassword(String userId, String token, String newPassword){
        if(token.isEmpty() || newPassword.isEmpty()){
            ui.showLowerRegularSnackBar(context, "Please enter your token and new password", R.color.danger_color, R.id.main);
            return;
        }

        mReset.setText(R.string.please_wait);

        mToken.setEnabled(false);
        mToken.setAlpha(0.5F);
        mNewPassword.setEnabled(false);
        mNewPassword.setAlpha(0.5F);

        int tokenNum = Integer.parseInt(token);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(tokenNum, newPassword);

        ResetPasswordApiService resetPasswordApiService = retrofit.create(ResetPasswordApiService.class);
        Call<Void> call = resetPasswordApiService.resetPassword(userId, resetPasswordRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    ui.showLowerRegularSnackBar(context, "Password reset successful, proceed to login", R.color.btn_acc, R.id.main);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    mReset.setText("Submit");
                    mToken.setEnabled(true);
                    mToken.setAlpha(1F);
                    mNewPassword.setEnabled(true);
                    mNewPassword.setAlpha(1F);
                    if (response.code() == 400){
                        ui.showLowerRegularSnackBar(context, "Invalid or expired token", R.color.danger_color, R.id.main);
                    } else if (response.code() == 401) {
                        ui.showLowerRegularSnackBar(context, "New password cannot be the same as previous password", R.color.danger_color, R.id.main);
                    } else {
                        ui.showLowerRegularSnackBar(context, "Failed to send code, Please try again later", R.color.danger_color, R.id.main);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                mReset.setText("Submit");
                mToken.setEnabled(true);
                mToken.setAlpha(1F);
                mNewPassword.setEnabled(true);
                mNewPassword.setAlpha(1F);
                ui.showLowerRegularSnackBar(context, "Error: " + t.getMessage(), R.color.danger_color, R.id.main);
                Log.e("PasswordResetError", "Request failed: " + t.getMessage());
            }
        });
    }
}