package com.hey.payment.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferRequest {
    private String targetId;
    private String softToken;
    private long amount;
    private String message;
}
