package com.hey.authentication.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UserDTO user;
    private String accessToken;
//    private String refreshToken;
    private String tokenType;
}
