package com.hey.payment.dto.chat_system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TransferMessageRequest {
    String sourceId;
    String targetId;
    long amount;
    String message;
    String createdAt;
}
