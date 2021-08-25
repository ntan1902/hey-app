package com.hey.lucky.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifySoftTokenResponse {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoftTokenEncoded {
        String userId;
        long amount;
    }
    Boolean success;
    int code;
    String message;
    SoftTokenEncoded payload;
}
