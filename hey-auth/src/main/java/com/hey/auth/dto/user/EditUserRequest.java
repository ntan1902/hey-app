package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {
    @NotEmpty(message = "email must not be empty")
    private String email;

    @NotEmpty(message = "fullName must not be empty")
    private String fullName;

    @NotEmpty(message = "dateOfBirth must not be empty")
    private String dateOfBirth;

    @NotEmpty(message = "phoneNumber must not be empty")
    private String phoneNumber;
}
