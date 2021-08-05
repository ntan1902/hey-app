package com.hey.auth.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinAmountRequest {
    @NotEmpty(message = "pin must not be empty")
    private String pin;

    @Min(1000L)
    private long amount;
}