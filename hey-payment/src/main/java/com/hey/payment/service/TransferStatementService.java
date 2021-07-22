package com.hey.payment.service;

import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.CreateTransferResponse;
import com.hey.payment.entity.User;

public interface TransferStatementService {
    CreateTransferResponse createTransfer(User user, CreateTransferRequest createTransferRequest);
}
