package com.example.kitchen_appliances_android_java.Request;

public class ResendOTPRequest {
    private String email;
    private String type;

    public ResendOTPRequest() {
    }

    public ResendOTPRequest(String email, String type) {
        this.email = email;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
