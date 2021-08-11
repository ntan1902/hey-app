package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePinRequest {
    private String pin;
    private String confirmPin;
    private String oldPin;
}
