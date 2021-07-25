package com.hey.payment.dto.auth_system;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifySoftTokenRequest {
    private String softToken;
}
