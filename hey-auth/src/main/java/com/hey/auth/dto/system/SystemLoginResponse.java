package com.hey.auth.dto.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemLoginResponse {
    private String accessToken;
    //    private String refreshToken;
    private String tokenType;
}