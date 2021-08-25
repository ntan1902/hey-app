package com.hey.integration.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "uq_username", unique = true, columnList = "username"),
                @Index(name = "uq_email", unique = true, columnList = "email")
        }
)
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    @Column(name = "full_name")
    private String fullName;

    private String pin;

    private LocalDateTime dob;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="avatar")
    private String avatar;

    @Column(name="mini_avatar")
    private String miniAvatar;

    public User(String id, String username, String email, String password, String fullName, String pin, LocalDateTime dob, String phoneNumber, String avatar, String miniAvatar) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.pin = pin;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.miniAvatar = miniAvatar;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public LocalDateTime getDob() {
        return dob;
    }

    public void setDob(LocalDateTime dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMiniAvatar() {
        return miniAvatar;
    }

    public void setMiniAvatar(String miniAvatar) {
        this.miniAvatar = miniAvatar;
    }
}
