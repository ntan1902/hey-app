package com.hey.model;

public class EditProfileRequest {
    private String email;
    private String fullName;
    private String dob;
    private String phoneNumber;

    public EditProfileRequest() {
    }

    public EditProfileRequest(String email, String fullName, String dob, String phoneNumber) {
        this.email = email;
        this.fullName = fullName;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
