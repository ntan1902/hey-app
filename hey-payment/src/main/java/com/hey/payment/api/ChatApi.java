package com.hey.payment.api;

import com.hey.payment.dto.chat_service.TransferMessageRequest;
import org.springframework.http.HttpEntity;

public interface ChatApi {
    boolean createTransferMessage(TransferMessageRequest transferMessageRequest);
}
