package com.hey.model;

public class EditProfileRequest {
    private String email;
    private String fullName;

    public EditProfileRequest() {
    }

    public EditProfileRequest(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
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
}
