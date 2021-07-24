package com.hey.payment.dto.chat_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMessageRequest {
    long sourceId;
    long targetId;
    long amount;
    String message;
    String createdAt;
}
