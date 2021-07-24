package com.hey.payment.dto.chat_service;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class TransferMessageRequest {
    long sourceId;
    long targetId;
    long amount;
    String message;
    LocalDateTime createdAt;
}
