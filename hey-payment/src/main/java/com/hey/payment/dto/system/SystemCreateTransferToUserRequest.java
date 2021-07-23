package com.hey.payment.dto.system;

import lombok.Data;

@Data
public class SystemCreateTransferToUserRequest {
    private long walletId;
    private long recieverId;
    private long amount;
    private String message;
}
