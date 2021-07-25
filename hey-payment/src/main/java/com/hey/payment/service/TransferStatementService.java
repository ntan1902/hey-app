package com.hey.payment.service;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.TopUpRequest;
import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;

import java.util.List;

public interface TransferStatementService {
    ApiResponse createTransfer(User user, CreateTransferRequest createTransferRequest);

    void topUp(User user, TopUpRequest topupRequest);

    List<TransferStatementDTO> getTransferStatementOfUser(long userId);

    ApiResponse systemCreateTransferToUser(System system, SystemCreateTransferToUserRequest request);

    ApiResponse systemCreateTransferFromUser(System system, SystemCreateTransferFromUserRequest request);
}
