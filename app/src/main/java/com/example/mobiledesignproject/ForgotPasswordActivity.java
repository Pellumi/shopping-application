package com.example.mobiledesignproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobiledesignproject.api.SendEmailApiService;
import com.example.mobiledesignproject.model.RecoveryEmail;
import com.example.mobiledesignproject.network.RetrofitClient;
import com.example.mobiledesignproject.ui.UIMethods;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email_address, matric_number;
    Button mSubmit;
    Context context = ForgotPasswordActivity.this;
    private final UIMethods ui = new UIMethods();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryColor));

        matric_number = findViewById(R.id.matric_no);
        email_address = findViewById(R.id.email_address);
        mSubmit = findViewById(R.id.submit_btn);

        ui.changeUI(email_address);

        mSubmit.setOnClickListener(v -> {
//            Toast.makeText(context, "This will be available in the next update", Toast.LENGTH_SHORT).show();
            String user_id = ui.replaceSlashWithHyphen(matric_number.getText().toString().trim());
            String user_email = email_address.getText().toString().trim();
            sendRecoveryEmail(user_id, user_email);
        });
    }

    private void sendRecoveryEmail(String userId, String email){
        if(email.isEmpty() || userId.isEmpty()){
            ui.showLowerRegularSnackBar(context, "Please enter an email address and matric number", R.color.danger_color, R.id.main);
            return;
        }

        mSubmit.setText(R.string.please_wait);

        email_address.setEnabled(false);
        email_address.setAlpha(0.5F);
        matric_number.setEnabled(false);
        matric_number.setAlpha(0.5F);

        RecoveryEmail recoveryEmail = new RecoveryEmail(email);

        Retrofit retrofit = RetrofitClient.getClient();
        SendEmailApiService sendEmailApiService = retrofit.create(SendEmailApiService.class);
        Call<Void> call = sendEmailApiService.sendRecoveryEmail(userId, recoveryEmail);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    ui.showLowerRegularSnackBar(context, "A Security code has been sent to your email, proceed to reset your password", R.color.btn_acc, R.id.main);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        intent = new Intent(context, PasswordResetActivity.class);
                        intent.putExtra("user-id", userId);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    mSubmit.setText("Submit");
                    email_address.setEnabled(true);
                    email_address.setAlpha(1F);
                    matric_number.setEnabled(true);
                    matric_number.setAlpha(1F);
                    ui.showLowerRegularSnackBar(context, "Failed to send code, Please review your email and try again", R.color.danger_color, R.id.main);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                mSubmit.setText("Submit");
                email_address.setEnabled(true);
                email_address.setAlpha(1F);
                matric_number.setEnabled(true);
                matric_number.setAlpha(1F);
                ui.showLowerRegularSnackBar(context, "Error: " + t.getMessage(), R.color.danger_color, R.id.main);
                Log.e("ForgotPasswordActivity", "Error to send code: " + t.getMessage(), t);
            }
        });
    }
}