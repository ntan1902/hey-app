package com.hey.payment.service;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserResponse;
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
    void createTransfer(User user, CreateTransferRequest createTransferRequest) throws SoftTokenAuthorizeException, NegativeAmountException, MaxAmountException, HaveNoWalletException,  SourceAndTargetAreTheSameException,  BalanceNotEnoughException, MaxBalanceException;

    void topUp(User user, TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException;

    List<TransferStatementDTO> getTransferStatementOfUser(String userId, int page, int size) throws HaveNoWalletException, DatabaseHasErr, ApiErrException;

    void systemCreateTransferToUser(System system, SystemCreateTransferToUserRequest request) throws NegativeAmountException, MaxAmountException,  HaveNoWalletException, BalanceNotEnoughException, MaxBalanceException;

    SystemCreateTransferFromUserResponse systemCreateTransferFromUser(System system, SystemCreateTransferFromUserRequest request) throws SoftTokenAuthorizeException, NegativeAmountException,  MaxAmountException,  BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException;
}
