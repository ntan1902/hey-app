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
    @NotEmpty(message = "Pin must not be empty")
    private String pin;

    @Min(value = 1000L, message = "Min amount is 1000")
    private long amount;
}