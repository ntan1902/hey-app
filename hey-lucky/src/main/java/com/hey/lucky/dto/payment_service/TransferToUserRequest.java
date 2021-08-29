package com.hey.lucky.dto.payment_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferToUserRequest {
    private long walletId;
    private String receiverId;
    private long amount;
    private String message;
    private String transferType;
}
