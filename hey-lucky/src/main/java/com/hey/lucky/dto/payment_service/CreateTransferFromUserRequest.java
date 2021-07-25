package com.hey.lucky.dto.payment_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferFromUserRequest {
    private long userId;
    private long walletId;
    private String softToken;
    private String message;
}