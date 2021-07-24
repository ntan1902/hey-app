package com.hey.payment.service;

import com.hey.payment.api.AuthApi;
import com.hey.payment.api.ChatApi;
import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.constant.TransferStatus;
import com.hey.payment.constant.TransferType;
import com.hey.payment.dto.auth_service.OwnerInfo;
import com.hey.payment.dto.auth_service.SoftTokenEncoded;
import com.hey.payment.dto.auth_service.SystemInfo;
import com.hey.payment.dto.auth_service.UserInfo;
import com.hey.payment.dto.chat_service.TransferMessageRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.TopupRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public void createTransfer(User user, CreateTransferRequest createTransferRequest) {
        log.info("User {} transfer to user {} with soft token {}", user.getId(), createTransferRequest.getTargetId(), createTransferRequest.getSoftToken());

        ApiResponse<SoftTokenEncoded> apiResponse = authApi.verifySoftToken(createTransferRequest.getSoftToken());
        if (!apiResponse.isSuccess()){
            throw new SoftTokenAuthorizeException(apiResponse.getMessage());
        }
        SoftTokenEncoded softTokenEncoded = apiResponse.getPayload();
        if (softTokenEncoded.getUserId()!=user.getId()){
            throw new SoftTokenAuthorizeException("Unauthorized!");
        }
        // 1. Kiểm tra user1 và user2 có wallet hay không
        Wallet s = walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> {
                    throw new HaveNoWalletException();
                });
        Wallet t = walletRepository.findByOwnerIdAndRefFrom(createTransferRequest.getTargetId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> {
                    throw new WrongTargetException();
                });

        // 2. Tạo transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(s.getId())
                .targetId(t.getId())
                .amount(softTokenEncoded.getAmount())
                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .build();

        transferStatementRepository.save(transferStatement);

        try {
            transferMoney(s.getId(), t.getId(), softTokenEncoded.getAmount());
        } catch (BalanceNotEnoughException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            throw exception;
        }

        transferStatement.setStatus(TransferStatus.SUCCESS);
        transferStatementRepository.save(transferStatement);

        chatApi.createTransferMessage(TransferMessageRequest.builder()
                .sourceId(user.getId())
                .targetId(createTransferRequest.getTargetId())
                .amount(softTokenEncoded.getAmount())
                .message(createTransferRequest.getMessage())
                .createdAt(transferStatement.getCreatedAt().toString())
                .build());
    }

    @Override
    public void systemCreateTransferToUser(System system, SystemCreateTransferToUserRequest request) {
        log.info("System use wallet {} transfer {} to user {}", request.getWalletId(),request.getAmount(),request.getRecieverId());
        Wallet s = walletRepository.findWalletByIdAndOwnerId(request.getWalletId(),system.getId())
                .orElseThrow(() -> {
                    throw new WrongSourceException();
                });
        Wallet t = walletRepository.findByOwnerIdAndRefFrom(request.getRecieverId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> {
                    throw new WrongTargetException();
                });

        // 2. Tạo transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(s.getId())
                .targetId(t.getId())
                .amount(request.getAmount())
                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .build();

        transferStatementRepository.save(transferStatement);

        try {
            transferMoney(s.getId(), t.getId(), request.getAmount());
        } catch (BalanceNotEnoughException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            throw exception;
        }

        transferStatement.setStatus(TransferStatus.SUCCESS);
        transferStatementRepository.save(transferStatement);
    }

    @Override
    public void systemCreateTransferFromUser(System system, SystemCreateTransferFromUserRequest request) {
        log.info("System transfer from user {} with soft token {}", request.getUserId(), request.getSoftToken());
        ApiResponse<SoftTokenEncoded> apiResponse = authApi.verifySoftToken(request.getSoftToken());
        if (!apiResponse.isSuccess()){
            throw new SoftTokenAuthorizeException(apiResponse.getMessage());
        }
        SoftTokenEncoded softTokenEncoded = apiResponse.getPayload();
        if (softTokenEncoded.getUserId()!=request.getUserId()){
            throw new SoftTokenAuthorizeException("Unauthorized!");
        }
        // 1. Kiểm tra user1 và user2 có wallet hay không
        Wallet s = walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> {
                    throw new WrongSourceException();
                });
        Wallet t = walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())
                .orElseThrow(() -> {
                    throw new WrongTargetException();
                });

        // 2. Tạo transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(s.getId())
                .targetId(t.getId())
                .amount(softTokenEncoded.getAmount())
                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .build();

        transferStatementRepository.save(transferStatement);

        try {
            transferMoney(s.getId(), t.getId(), softTokenEncoded.getAmount());
        } catch (BalanceNotEnoughException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            throw exception;
        }

        transferStatement.setStatus(TransferStatus.SUCCESS);
        transferStatementRepository.save(transferStatement);
    }

    @Transactional
    public void transferMoney(long sourceWalletId, long targetWalletId, long amount) {
        Wallet sourceWallet = walletRepository.getWalletById(sourceWalletId).orElseThrow(() -> {
            throw new WrongSourceException();
        });
        Wallet targetWallet = walletRepository.getWalletById(targetWalletId).orElseThrow(() -> {
            throw new WrongTargetException();
        });
        long sourceBalance = sourceWallet.getBalance();
        long targetBalance = targetWallet.getBalance();

        if (sourceBalance < amount) {
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
    public void topUp(User user, TopupRequest topupRequest) {
        log.info("Topup for user {} by bank {} with {}", user.getId(), topupRequest.getBankId(), topupRequest.getAmount());
        Wallet bankWallet = walletRepository.findByOwnerIdAndRefFrom(topupRequest.getBankId(), OwnerWalletRefFrom.SYSTEMS)
                .orElseThrow(() -> {
                    throw new BankInvalidException();
                });
        Wallet userWallet = walletRepository.getByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> {
                    throw new HaveNoWalletException();
                });
        userWallet.setBalance(userWallet.getBalance() + topupRequest.getAmount());
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(bankWallet.getId())
                .targetId(userWallet.getId())
                .amount(topupRequest.getAmount())
                .transferFee(calculateTransferFee())
                .transferType(TransferType.TOPUP)
                .build();
        transferStatementRepository.save(transferStatement);
    }

    @Override
    public List<TransferStatementDTO> getTransferStatementOfUser(long userId) {
        Wallet wallet = walletRepository.findByOwnerIdAndRefFrom(userId, OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> {
                    throw new HaveNoWalletException();
                });

        List<TransferStatement> transferStatements = transferStatementRepository.findAllBySourceIdOrTargetId(wallet.getId());

        return listTransferStatement2ListTransferStatementDTO(transferStatements);
    }

    private List<TransferStatementDTO> listTransferStatement2ListTransferStatementDTO(List<TransferStatement> transferStatements){
        return transferStatements.stream().map(transferStatement -> {
            TransferStatementDTO transferStatementDTO = transferStatementMapper.ts2TsDTO(transferStatement);
            Wallet sourceWallet = walletRepository.findById(transferStatement.getSourceId())
                    .orElseThrow(() -> {
                        throw new DatabaseHasErr();
                    });
            OwnerInfo sourceOwner = getOwnerInfo(sourceWallet.getOwnerId(),sourceWallet.getRefFrom());
            transferStatementDTO.setSource(sourceOwner);
            Wallet targetWallet = walletRepository.findById(transferStatement.getTargetId())
                    .orElseThrow(() -> {
                        throw new DatabaseHasErr();
                    });
            OwnerInfo targetOwner = getOwnerInfo(targetWallet.getOwnerId(), targetWallet.getRefFrom());
            transferStatementDTO.setTarget(targetOwner);
            return transferStatementDTO;
        }).collect(Collectors.toList());
    }

    private OwnerInfo getOwnerInfo(long ownerId, String refFrom){
        if (refFrom.equals(OwnerWalletRefFrom.USERS)) {
            ApiResponse<UserInfo> apiResponse = authApi.getUserInfo(ownerId);
            if (apiResponse.isSuccess()){
                return apiResponse.getPayload();
            } else {
                throw new ApiErrException(apiResponse.getMessage());
            }
        } else {
            ApiResponse<SystemInfo> apiResponse = authApi.getSystemInfo(ownerId);
            if (apiResponse.isSuccess()){
                return apiResponse.getPayload();
            } else {
                throw new ApiErrException(apiResponse.getMessage());
            }
        }
    }

    private Long calculateTransferFee() {
        return 0L;
    }
}
