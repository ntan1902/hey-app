package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotEmpty(message = "Password must not be empty")
    private String password;

    @NotEmpty(message = "Confirm password must not be empty")
    private String confirmPassword;

    @NotEmpty(message = "Old password must not be empty")
    private String oldPassword;
}
