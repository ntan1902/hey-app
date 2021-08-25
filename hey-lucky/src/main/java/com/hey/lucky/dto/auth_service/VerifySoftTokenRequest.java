package com.hey.lucky.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifySoftTokenRequest {
    private String softToken;
}
