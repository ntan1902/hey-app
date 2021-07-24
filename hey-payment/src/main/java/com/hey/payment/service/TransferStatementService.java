package com.hey.payment.service;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.TopupRequest;
import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;

import java.util.List;

public interface TransferStatementService {
    void createTransfer(User user, CreateTransferRequest createTransferRequest);

    void topUp(User user, TopupRequest topupRequest);

    List<TransferStatementDTO> getTransferStatementOfUser(long userId);

    void systemCreateTransferToUser(System system, SystemCreateTransferToUserRequest request);

    void systemCreateTransferFromUser(System system, SystemCreateTransferFromUserRequest request);
}
