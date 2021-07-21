package com.hey.payment.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    String systemName;
    String systemKey;
}
