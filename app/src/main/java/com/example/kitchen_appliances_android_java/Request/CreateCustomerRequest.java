package com.example.kitchen_appliances_android_java.Request;

import java.io.Serializable;

public class CreateCustomerRequest implements Serializable {
    private String fullname;
    private String phoneNumber;
    private String address;
    private String email;
    private String password;

    public CreateCustomerRequest() {
    }

    public CreateCustomerRequest(String fullname, String phoneNumber, String address, String email, String password) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
