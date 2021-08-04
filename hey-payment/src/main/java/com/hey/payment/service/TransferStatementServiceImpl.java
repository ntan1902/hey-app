package com.hey.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.payment.api.AuthApi;
import com.hey.payment.api.ChatApi;
import com.hey.payment.constant.MoneyConstant;
import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.constant.TransferStatus;
import com.hey.payment.constant.TransferType;
import com.hey.payment.dto.auth_system.GetSystemInfoResponse;
import com.hey.payment.dto.auth_system.GetUserInfoResponse;
import com.hey.payment.dto.auth_system.OwnerInfo;
import com.hey.payment.dto.auth_system.VerifySoftTokenResponse;
import com.hey.payment.dto.chat_system.TransferMessageRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserResponse;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.TopUpRequest;
import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.TransferStatement;
import com.hey.payment.entity.User;
import com.hey.payment.entity.Wallet;
import com.hey.payment.exception_handler.exception.*;
import com.hey.payment.mapper.TransferStatementMapper;
import com.hey.payment.repository.TransferStatementRepository;
import com.hey.payment.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class TransferStatementServiceImpl implements TransferStatementService {

    private final TransferStatementRepository transferStatementRepository;

    private final WalletRepository walletRepository;

    private final TransferStatementMapper transferStatementMapper;

    private final ChatApi chatApi;

    private final AuthApi authApi;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse<?> createTransfer(User user, CreateTransferRequest createTransferRequest) throws SoftTokenAuthorizeException, NegativeAmountException, MaxAmountException, HaveNoWalletException, WrongTargetException, SourceAndTargetAreTheSameException {
        log.info("User {} transfer to user {} with soft token {}", user.getId(), createTransferRequest.getTargetId(), createTransferRequest.getSoftToken());

        String sourceId = user.getId();
        String targetId = createTransferRequest.getTargetId();
        String message = createTransferRequest.getMessage();
        String softToken = createTransferRequest.getSoftToken();

        // Check if sourceId and targetId are the same
        if(sourceId.equals(targetId)) {
            throw new SourceAndTargetAreTheSameException("Can not transfer yourself");
        }


        // Authorize Soft Token
        VerifySoftTokenResponse apiResponse = authApi.verifySoftToken(softToken);
        if (!apiResponse.getSuccess()) throw new SoftTokenAuthorizeException(apiResponse.getMessage());

        // Check User Id
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = apiResponse.getPayload();
        if (!softTokenEncoded.getUserId().equals(sourceId)) throw new SoftTokenAuthorizeException("Unauthorized!");

        long amount = softTokenEncoded.getAmount();
        // Check amount is negative
        if (amount < 0) throw new NegativeAmountException();

        if (isMaxAmount(amount)) throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);

        // Check user1, user2 whether have wallet
        Wallet sourceWallet = walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(HaveNoWalletException::new);
        Wallet targetWallet = walletRepository.findByOwnerIdAndRefFrom(targetId, OwnerWalletRefFrom.USERS)
                .orElseThrow(WrongTargetException::new);


        // Create transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(sourceWallet.getId())
                .targetId(targetWallet.getId())
                .amount(amount)
                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .createdAt(LocalDateTime.now())
                .message(createTransferRequest.getMessage())
                .transferType(TransferType.TRANSFER)
                .build();
        transferStatementRepository.save(transferStatement);

        // Transfer money
        try {
            transferMoney(sourceWallet.getId(), targetWallet.getId(), amount);
        } catch (BalanceNotEnoughException | MaxBalanceException | WrongSourceException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            return setApiResponse(false, 400, exception.getMessage(), "");
        }

        // Transfer money successfully
        transferStatement.setStatus(TransferStatus.SUCCESS);
        transferStatementRepository.save(transferStatement);

        log.info("Send api transfer message to hey-chat: ");
        chatApi.createTransferMessage(TransferMessageRequest.builder()
                .sourceId(sourceId)
                .targetId(targetId)
                .amount(amount)
                .message(message)
                .createdAt(transferStatement.getCreatedAt().toString())
                .build());
        return setApiResponse(true, 201, "Transfer success", "");
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse<?> systemCreateTransferToUser(System system, SystemCreateTransferToUserRequest request) throws NegativeAmountException, MaxAmountException, WrongSourceException, HaveNoWalletException {
        log.info("System use wallet {} transfer {} to user {}", request.getWalletId(), request.getAmount(), request.getReceiverId());

        long amount = request.getAmount();
        // Check amount is negative
        if (amount < 0) {
            throw new NegativeAmountException();
        }

        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        // Check 2 wallets are exist
        Wallet s = walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())
                .orElseThrow(WrongSourceException::new);
        Wallet t = walletRepository.findByOwnerIdAndRefFrom(request.getReceiverId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(HaveNoWalletException::new);


        // Create transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(s.getId())
                .targetId(t.getId())
                .amount(request.getAmount())
                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .message(request.getMessage())
                .transferType(TransferType.TRANSFER)
                .build();

        transferStatementRepository.save(transferStatement);

        try {
            transferMoney(s.getId(), t.getId(), request.getAmount());
        } catch (BalanceNotEnoughException | MaxBalanceException | WrongTargetException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            return setApiResponse(false, 400, exception.getMessage(), "");
        }

        transferStatement.setStatus(TransferStatus.SUCCESS);
        transferStatementRepository.save(transferStatement);
        return setApiResponse(true, 201, "Transfer success", "");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse<?> systemCreateTransferFromUser(System system, SystemCreateTransferFromUserRequest request) throws SoftTokenAuthorizeException, NegativeAmountException, WrongSourceException, MaxAmountException, WrongTargetException {
        log.info("System transfer from user {} with soft token {}", request.getUserId(), request.getSoftToken());
        // Verify soft token
        VerifySoftTokenResponse apiResponse = authApi.verifySoftToken(request.getSoftToken());
        if (!apiResponse.getSuccess()) {
            throw new SoftTokenAuthorizeException(apiResponse.getMessage());
        }
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = apiResponse.getPayload();
        if (!softTokenEncoded.getUserId().equals(request.getUserId())) {
            throw new SoftTokenAuthorizeException("Unauthorized!");
        }

        long amount = softTokenEncoded.getAmount();
        // Check amount is negative
        if (amount < 0) {
            throw new NegativeAmountException();
        }

        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        // Check 2 wallets are exist and system have permission
        Wallet source = walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(WrongSourceException::new);
        Wallet target = walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())
                .orElseThrow(WrongTargetException::new);

        // Create transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(source.getId())
                .targetId(target.getId())
                .amount(softTokenEncoded.getAmount())
                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .message(request.getMessage())
                .transferType(TransferType.TRANSFER)
                .build();

        transferStatementRepository.save(transferStatement);

        try {
            transferMoney(source.getId(), target.getId(), softTokenEncoded.getAmount());
        } catch (BalanceNotEnoughException | MaxBalanceException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            return setApiResponse(false, 400, exception.getMessage(), "");
        }

        transferStatement.setStatus(TransferStatus.SUCCESS);
        transferStatementRepository.save(transferStatement);

        return setApiResponse(true, 201, "Transfer success", new SystemCreateTransferFromUserResponse(amount));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void transferMoney(long sourceWalletId, long targetWalletId, long amount) throws WrongSourceException, WrongTargetException, MaxBalanceException, BalanceNotEnoughException {
        // Get and lock 2 wallets
        Wallet sourceWallet = walletRepository.getWalletById(sourceWalletId)
                .orElseThrow(WrongSourceException::new);

        Wallet targetWallet = walletRepository.getWalletById(targetWalletId)
                .orElseThrow(WrongTargetException::new);
        long sourceBalance = sourceWallet.getBalance();
        long targetBalance = targetWallet.getBalance();
        if (isMaxBalance(targetBalance + amount)) {
            throw new MaxBalanceException();
        }
        if (sourceBalance >= amount) {
            sourceWallet.setBalance(sourceBalance - amount);
            targetWallet.setBalance(targetBalance + amount);
            walletRepository.save(sourceWallet);
            walletRepository.save(targetWallet);
        } else {
            throw new BalanceNotEnoughException();
        }
    }


    @Override
    @Transactional
    public void topUp(User user, TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException {
        log.info("TopUp for user {} by bank {} with {}", user.getId(), topupRequest.getBankId(), topupRequest.getAmount());

        // Check 2 wallets are exist.
        long amount = topupRequest.getAmount();
        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        Wallet bankWallet = walletRepository.findByOwnerIdAndRefFrom(topupRequest.getBankId(), OwnerWalletRefFrom.SYSTEMS)
                .orElseThrow(BankInvalidException::new);
        Wallet userWallet = walletRepository.getByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(HaveNoWalletException::new);

        if (isMaxBalance(userWallet.getBalance() + amount)) {
            throw new MaxBalanceException();
        }

        userWallet.setBalance(userWallet.getBalance() + amount);
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(bankWallet.getId())
                .targetId(userWallet.getId())
                .amount(topupRequest.getAmount())
                .transferFee(calculateTransferFee())
                .transferType(TransferType.TOPUP)
                .status(TransferStatus.SUCCESS)
                .message("Topup from bank " + topupRequest.getBankId())
                .build();
        transferStatementRepository.save(transferStatement);
    }

    @Override
    public List<TransferStatementDTO> getTransferStatementOfUser(String userId) throws HaveNoWalletException, DatabaseHasErr, ApiErrException {
        Wallet wallet = walletRepository.findByOwnerIdAndRefFrom(userId, OwnerWalletRefFrom.USERS)
                .orElseThrow(HaveNoWalletException::new);

        List<TransferStatement> transferStatements = transferStatementRepository.findAllBySourceIdOrTargetId(wallet.getId());

        return listTransferStatement2ListTransferStatementDTO(transferStatements);
    }

    public List<TransferStatementDTO> listTransferStatement2ListTransferStatementDTO(List<TransferStatement> transferStatements) throws DatabaseHasErr, ApiErrException {
        List<TransferStatementDTO> transferStatementDTOs = new ArrayList<>();

        for (TransferStatement transferStatement : transferStatements) {
            TransferStatementDTO transferStatementDTO = transferStatementMapper.ts2TsDTO(transferStatement);
            Wallet sourceWallet = walletRepository.findById(transferStatement.getSourceId())
                    .orElseThrow(DatabaseHasErr::new);
            OwnerInfo sourceOwner = getOwnerInfo(sourceWallet.getOwnerId(), sourceWallet.getRefFrom());
            transferStatementDTO.setSource(sourceOwner);

            Wallet targetWallet = walletRepository.findById(transferStatement.getTargetId())
                    .orElseThrow(DatabaseHasErr::new);

            OwnerInfo targetOwner = getOwnerInfo(targetWallet.getOwnerId(), targetWallet.getRefFrom());
            transferStatementDTO.setTarget(targetOwner);

            transferStatementDTOs.add(transferStatementDTO);
        }
        return transferStatementDTOs;
    }

    public OwnerInfo getOwnerInfo(String ownerId, String refFrom) throws ApiErrException {
        ObjectMapper mapper = new ObjectMapper();
        if (refFrom.equals(OwnerWalletRefFrom.USERS)) {
            GetUserInfoResponse apiResponse = authApi.getUserInfo(ownerId);
            if (apiResponse.getSuccess()) {
                return mapper.convertValue(apiResponse.getPayload(), GetUserInfoResponse.UserInfo.class);
            } else {
                throw new ApiErrException(apiResponse.getMessage());
            }
        } else {
            GetSystemInfoResponse apiResponse = authApi.getSystemInfo(ownerId);
            if (apiResponse.getSuccess()) {
                return mapper.convertValue(apiResponse.getPayload(), GetSystemInfoResponse.SystemInfo.class);
            } else {
                throw new ApiErrException(apiResponse.getMessage());
            }
        }
    }

    private <T> ApiResponse<?> setApiResponse(boolean success, int code, String message, T payload) {
        return ApiResponse.builder()
                .success(success)
                .code(code)
                .message(message)
                .payload(payload)
                .build();
    }
    public boolean isMaxAmount(long amount) {
        return amount > MoneyConstant.MAX_AMOUNT;
    }

    public boolean isMaxBalance(long balance) {
        return balance > MoneyConstant.MAX_BALANCE;
    }

    public Long calculateTransferFee() {
        return 0L;
    }
}
