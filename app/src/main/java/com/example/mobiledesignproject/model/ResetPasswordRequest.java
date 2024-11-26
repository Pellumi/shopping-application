package com.example.mobiledesignproject.model;

public class ResetPasswordRequest {
    private int token;
    private String newPassword;

    public ResetPasswordRequest(int token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public int getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
