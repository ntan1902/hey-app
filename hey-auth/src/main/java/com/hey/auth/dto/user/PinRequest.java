package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinRequest {
    @NotEmpty(message = "Pin must not be empty")
    @Size(min = 6, max = 6, message = "Pin's length must be 6")
    private String pin;
}