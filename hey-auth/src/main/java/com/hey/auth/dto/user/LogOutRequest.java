package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOutRequest {
    @NotEmpty(message = "accessToken must be not empty")
    private String accessToken;

    @NotEmpty(message = "refreshToken must be not empty")
    private String refreshToken;
}