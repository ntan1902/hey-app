package com.hey.authentication.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemLoginResponse {
    private String accessToken;
    //    private String refreshToken;
    private String tokenType;
}