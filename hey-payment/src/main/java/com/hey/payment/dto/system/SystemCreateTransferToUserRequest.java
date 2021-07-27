package com.hey.payment.dto.system;

import lombok.Data;

@Data
public class SystemCreateTransferToUserRequest {
    private long walletId;
    private long receiverId;
    private long amount;
    private String message;
}
