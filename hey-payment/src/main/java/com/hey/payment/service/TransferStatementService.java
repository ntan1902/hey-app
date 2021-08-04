package com.hey.payment.service;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.TopUpRequest;
import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;
import com.hey.payment.exception_handler.exception.*;

import java.util.List;

public interface TransferStatementService {
    ApiResponse<?> createTransfer(User user, CreateTransferRequest createTransferRequest) throws SoftTokenAuthorizeException, NegativeAmountException, MaxAmountException, HaveNoWalletException, WrongTargetException, SourceAndTargetAreTheSameException;

    void topUp(User user, TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException;

    List<TransferStatementDTO> getTransferStatementOfUser(String userId) throws HaveNoWalletException, DatabaseHasErr, ApiErrException;

    ApiResponse<?> systemCreateTransferToUser(System system, SystemCreateTransferToUserRequest request) throws NegativeAmountException, MaxAmountException, WrongSourceException, HaveNoWalletException;

    ApiResponse<?> systemCreateTransferFromUser(System system, SystemCreateTransferFromUserRequest request) throws SoftTokenAuthorizeException, NegativeAmountException, WrongSourceException, MaxAmountException, WrongTargetException;
}
