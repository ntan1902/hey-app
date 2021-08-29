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
import com.hey.payment.utils.SystemUtil;
import com.hey.payment.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
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

    private final WalletService walletService;

    private final TransferStatementMapper transferStatementMapper;

    private final ChatApi chatApi;

    private final AuthApi authApi;

    private final SystemUtil systemUtil;

    private final UserUtil userUtil;

    private static final String SOURCE_HAS_NO_WALLET = "Source has no wallet";

    private static final String TARGET_HAS_NO_WALLET = "Target has no wallet";

    private static final String USER_HAS_NO_WALLET = "User has no wallet";

    private static final String UNAUTHORIZED = "Unauthorized!";

    public static final String AMOUNT_IN_SOFT_TOKEN_IS_NOT_EQUALS_AMOUNT_IN_REQUEST = "Amount in soft token is not equals amount in request";


    @Override
    public void createTransfer(CreateTransferRequest createTransferRequest) throws SoftTokenAuthorizeException, MinAmountException, MaxAmountException, SourceAndTargetAreTheSameException, BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        User user = userUtil.getCurrentUser();
        log.info("User {} transfer to user {} with soft token {}", user.getId(), createTransferRequest.getTargetId(), createTransferRequest.getSoftToken());

        String sourceId = user.getId();
        String targetId = createTransferRequest.getTargetId();
        String message = createTransferRequest.getMessage();
        String softToken = createTransferRequest.getSoftToken();

        // Check if sourceId and targetId are the same
        if (sourceId.equals(targetId)) {
            throw new SourceAndTargetAreTheSameException("Can not transfer yourself");
        }

        // Authorize Soft Token
        VerifySoftTokenResponse apiResponse = authApi.verifySoftToken(softToken);
        if (!Boolean.TRUE.equals(apiResponse.getSuccess())) {
            log.error("Can't authorize soft token");
            throw new SoftTokenAuthorizeException(apiResponse.getMessage());
        }


        // Check User Id
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = apiResponse.getPayload();
        if (!softTokenEncoded.getUserId().equals(sourceId)) {
            throw new SoftTokenAuthorizeException(UNAUTHORIZED);
        }

        if (softTokenEncoded.getAmount() != createTransferRequest.getAmount()) {
            throw new SoftTokenAuthorizeException(AMOUNT_IN_SOFT_TOKEN_IS_NOT_EQUALS_AMOUNT_IN_REQUEST);
        }

        long amount = softTokenEncoded.getAmount();
        // Check amount is negative
        if (amount < MoneyConstant.MIN_AMOUNT) {
            throw new MinAmountException();
        }

        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        // Check user1, user2 whether have wallet
        Wallet sourceWallet = walletRepository.findByOwnerIdAndRefFrom(sourceId, OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException(SOURCE_HAS_NO_WALLET));
        Wallet targetWallet = walletRepository.findByOwnerIdAndRefFrom(targetId, OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException(TARGET_HAS_NO_WALLET));


        // Create transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(sourceWallet.getId())
                .targetId(targetWallet.getId())
                .amount(amount)
//                .status(TransferStatus.PROCESSING)
                .transferFee(calculateTransferFee())
                .createdAt(LocalDateTime.now())
                .message(createTransferRequest.getMessage())
                .transferType(TransferType.TRANSFER)
                .build();
//        transferStatementRepository.save(transferStatement);

        // Transfer money
        try {
            walletService.transferMoney(sourceWallet.getId(), targetWallet.getId(), amount);

            // Transfer money successfully
            transferStatement.setStatus(TransferStatus.SUCCESS);
            transferStatementRepository.save(transferStatement);

            log.info("Send api transfer message to hey-chat: ");
            chatApi.createTransferMessage(
                    TransferMessageRequest.builder()
                            .sourceId(sourceId)
                            .targetId(targetId)
                            .amount(amount)
                            .message(message)
                            .createdAt(transferStatement.getCreatedAt().toString())
                            .build()
            );
        } catch (MaxBalanceException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            throw exception;
        }
    }

    @Override
    public void systemCreateTransferToUser(SystemCreateTransferToUserRequest request) throws MinAmountException, MaxAmountException, HaveNoWalletException, BalanceNotEnoughException, MaxBalanceException {
        System system = systemUtil.getCurrentSystem();
        log.info("System use wallet {} transfer {} to user {}", request.getWalletId(), request.getAmount(), request.getReceiverId());

        long amount = request.getAmount();
        // Check amount is negative
        if (amount < MoneyConstant.MIN_AMOUNT) {
            throw new MinAmountException();
        }

        // Check amount is maximum
        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        // Check 2 wallets are exist
        Wallet s = walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())
                .orElseThrow(() -> new HaveNoWalletException(SOURCE_HAS_NO_WALLET));
        Wallet t = walletRepository.findByOwnerIdAndRefFrom(request.getReceiverId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException(TARGET_HAS_NO_WALLET));


        // Create transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(s.getId())
                .targetId(t.getId())
                .amount(request.getAmount())
                .transferFee(calculateTransferFee())
                .message(request.getMessage())
                .transferType(request.getTransferType())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            walletService.transferMoney(s.getId(), t.getId(), request.getAmount());

            transferStatement.setStatus(TransferStatus.SUCCESS);
            transferStatementRepository.save(transferStatement);
        } catch (MaxBalanceException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);

            throw exception;
        }

    }

    @Override
    public SystemCreateTransferFromUserResponse systemCreateTransferFromUser(SystemCreateTransferFromUserRequest request) throws SoftTokenAuthorizeException, MinAmountException, MaxAmountException, BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        System system = systemUtil.getCurrentSystem();
        log.info("System transfer from user {} with soft token {}", request.getUserId(), request.getSoftToken());
        // Verify soft token
        VerifySoftTokenResponse apiResponse = authApi.verifySoftToken(request.getSoftToken());
        if (!Boolean.TRUE.equals(apiResponse.getSuccess())) {
            throw new SoftTokenAuthorizeException(apiResponse.getMessage());
        }
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = apiResponse.getPayload();
        if (!softTokenEncoded.getUserId().equals(request.getUserId())) {
            throw new SoftTokenAuthorizeException(UNAUTHORIZED);
        }

        if (softTokenEncoded.getAmount() != request.getAmount()) {
            throw new SoftTokenAuthorizeException(AMOUNT_IN_SOFT_TOKEN_IS_NOT_EQUALS_AMOUNT_IN_REQUEST);
        }

        long amount = softTokenEncoded.getAmount();
        // Check amount is negative
        if (amount < MoneyConstant.MIN_AMOUNT) {
            throw new MinAmountException();
        }

        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        // Check 2 wallets are exist and system have permission
        Wallet source = walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException(SOURCE_HAS_NO_WALLET));
        Wallet target = walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())
                .orElseThrow(() -> new HaveNoWalletException(TARGET_HAS_NO_WALLET));

        // Create transfer statement
        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(source.getId())
                .targetId(target.getId())
                .amount(softTokenEncoded.getAmount())
                .transferFee(calculateTransferFee())
                .message(request.getMessage())
                .transferType(request.getTransferType())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            walletService.transferMoney(source.getId(), target.getId(), softTokenEncoded.getAmount());
            transferStatement.setStatus(TransferStatus.SUCCESS);
            transferStatementRepository.save(transferStatement);
            return new SystemCreateTransferFromUserResponse(amount);
        } catch (MaxBalanceException exception) {
            transferStatement.setStatus(TransferStatus.FAIL);
            transferStatementRepository.save(transferStatement);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void topUp(TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException {
        User user = userUtil.getCurrentUser();
        log.info("TopUp for user {} by bank {} with {}", user.getId(), topupRequest.getBankId(), topupRequest.getAmount());

        // Check 2 wallets are exist.
        long amount = topupRequest.getAmount();
        if (isMaxAmount(amount)) {
            throw new MaxAmountException(MoneyConstant.MAX_AMOUNT);
        }

        Wallet bankWallet = walletRepository.findByOwnerIdAndRefFrom(topupRequest.getBankId(), OwnerWalletRefFrom.SYSTEMS)
                .orElseThrow(BankInvalidException::new);
        Wallet userWallet = walletRepository.findAndLockByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException(USER_HAS_NO_WALLET));

        if (isMaxBalance(userWallet.getBalance() + amount)) {
            throw new MaxBalanceException();
        }

        userWallet.setBalance(userWallet.getBalance() + amount);
        walletRepository.save(userWallet);

        TransferStatement transferStatement = TransferStatement.builder()
                .sourceId(bankWallet.getId())
                .targetId(userWallet.getId())
                .amount(topupRequest.getAmount())
                .transferFee(calculateTransferFee())
                .transferType(TransferType.TOPUP)
                .status(TransferStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .message("TopUp from bank " + topupRequest.getBankId())
                .build();
        transferStatementRepository.save(transferStatement);
    }

    @Override
    public List<TransferStatementDTO> getTransferStatementsOfUser(int offset, int limit) throws HaveNoWalletException, DatabaseHasErr, ApiErrException {
        log.info("Inside getTransferStatementsOfUser of TransferStatementServiceImpl: {}, {}", offset, limit);
        User user = userUtil.getCurrentUser();
        Wallet wallet = walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException(USER_HAS_NO_WALLET));

        List<TransferStatement> transferStatements =
                transferStatementRepository.findAllBySourceIdOrTargetIdOrderByCreatedAtDesc(
                        wallet.getId(),
                        offset,
                        limit
                );

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
            if (Boolean.TRUE.equals(apiResponse.getSuccess())) {
                return mapper.convertValue(apiResponse.getPayload(), GetUserInfoResponse.UserInfo.class);
            } else {
                throw new ApiErrException(apiResponse.getMessage());
            }
        } else {
            GetSystemInfoResponse apiResponse = authApi.getSystemInfo(ownerId);
            if (Boolean.TRUE.equals(apiResponse.getSuccess())) {
                return mapper.convertValue(apiResponse.getPayload(), GetSystemInfoResponse.SystemInfo.class);
            } else {
                throw new ApiErrException(apiResponse.getMessage());
            }
        }
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
