package com.hey.payment.dto.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemCreateTransferFromUserRequest {
    private String userId;
    private long walletId;
    private String softToken;
    private String message;
}
