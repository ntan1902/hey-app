package com.hey.payment.service;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserResponse;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.TopUpRequest;
import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.exception_handler.exception.*;

import java.util.List;

public interface TransferStatementService {
    void createTransfer(CreateTransferRequest createTransferRequest) throws SoftTokenAuthorizeException, NegativeAmountException, MaxAmountException, HaveNoWalletException,  SourceAndTargetAreTheSameException,  BalanceNotEnoughException, MaxBalanceException;

    void topUp(TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException;

    List<TransferStatementDTO> getTransferStatementOfUser(int page, int size) throws HaveNoWalletException, DatabaseHasErr, ApiErrException;

    void systemCreateTransferToUser(SystemCreateTransferToUserRequest request) throws NegativeAmountException, MaxAmountException,  HaveNoWalletException, BalanceNotEnoughException, MaxBalanceException;

    SystemCreateTransferFromUserResponse systemCreateTransferFromUser(SystemCreateTransferFromUserRequest request) throws SoftTokenAuthorizeException, NegativeAmountException,  MaxAmountException,  BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException;
}
