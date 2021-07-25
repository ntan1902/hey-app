package com.hey.payment.api;

import com.hey.payment.dto.chat_system.TransferMessageRequest;

public interface ChatApi {
    boolean createTransferMessage(TransferMessageRequest transferMessageRequest);
}
