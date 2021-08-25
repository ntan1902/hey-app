package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "This is not an email")
    private String email;

    @NotEmpty(message = "Full name must not be empty")
    private String fullName;

    @NotEmpty(message = "Date of birth must not be empty")
    private String dob;

    @NotEmpty(message = "Phone number must not be empty")
    private String phoneNumber;
}
