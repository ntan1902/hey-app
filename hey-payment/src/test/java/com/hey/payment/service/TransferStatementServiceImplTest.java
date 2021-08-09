package com.hey.payment.service;

import com.hey.payment.api.AuthApi;
import com.hey.payment.api.ChatApi;
import com.hey.payment.constant.MoneyConstant;
import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.constant.TransferStatus;
import com.hey.payment.dto.auth_system.GetSystemInfoResponse;
import com.hey.payment.dto.auth_system.GetUserInfoResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransferStatementServiceImplTest {
    @InjectMocks
    private TransferStatementServiceImpl underTest;

    @Mock
    private TransferStatementRepository transferStatementRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private TransferStatementMapper transferStatementMapper;

    @Mock
    private ChatApi chatApi;

    @Mock
    private AuthApi authApi;


    @Test
    void createTransfer() throws BalanceNotEnoughException, MaxBalanceException, NegativeAmountException, MaxAmountException, SourceAndTargetAreTheSameException, SoftTokenAuthorizeException, HaveNoWalletException {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(user.getId())
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId(request.getTargetId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getTargetId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(targetWallet));

        doNothing().when(walletService).transferMoney(sourceWallet.getId(), targetWallet.getId(), softTokenEncoded.getAmount());


        when(chatApi.createTransferMessage(any(TransferMessageRequest.class))).thenReturn(true);

        // when
        underTest.createTransfer(user, request);

        // then
        verify(walletService, times(1)).transferMoney(sourceWallet.getId(), targetWallet.getId(), softTokenEncoded.getAmount());
        verify(chatApi, times(1)).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));

    }

    @Test
    void createTransferWillThrowSourceAndTargetAreTheSameException() {
        // given
        User user = User.builder()
                .id("uuid")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid")
                .softToken("amountPin")
                .message("Hello")
                .build();

        // when

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(SourceAndTargetAreTheSameException.class)
                .hasMessageContaining("Can not transfer yourself");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
    }

    @Test
    void createTransferWillThrowSoftTokenAuthorizeException() {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(false, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(SoftTokenAuthorizeException.class);

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void createTransferWillThrowUnauthorized() {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(SoftTokenAuthorizeException.class)
                .hasMessageContaining("Unauthorized!");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void createTransferWillThrowNegativeAmountException() {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", -5L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(NegativeAmountException.class)
                .hasMessageContaining("Negative amount");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void createTransferWillThrowMaxAmountException() {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 51_000_000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(MaxAmountException.class)
                .hasMessageContaining("Transaction limit is " + MoneyConstant.MAX_AMOUNT);

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void createTransferWillThrowSourceHaveNoWallet() {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);
        when(walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Source has no wallet");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
    }

    @Test
    void createTransferWillThrowTargetHaveNoWallet() {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(user.getId())
                .refFrom("users")
                .balance(5000000L)
                .build();


        when(walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getTargetId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Target has no wallet");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
    }

    @Test
    void createTransferWillThrowBalanceNotEnoughException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(user.getId())
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId(request.getTargetId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getTargetId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(targetWallet));

        doThrow(new BalanceNotEnoughException()).when(walletService).transferMoney(sourceWallet.getId(), targetWallet.getId(), softTokenEncoded.getAmount());

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(BalanceNotEnoughException.class)
                .hasMessageContaining("Your balance is not enough");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));

    }

    @Test
    void createTransferWillThrowMaxBalanceException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        User user = User.builder()
                .id("uuid1")
                .build();

        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("uuid2")
                .softToken("amountPin")
                .message("Hello")
                .build();

        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(user.getId())
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId(request.getTargetId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getTargetId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(targetWallet));

        doThrow(new MaxBalanceException()).when(walletService).transferMoney(sourceWallet.getId(), targetWallet.getId(), softTokenEncoded.getAmount());

        // then
        assertThatThrownBy(() -> underTest.createTransfer(user, request))
                .isInstanceOf(MaxBalanceException.class)
                .hasMessageContaining("Target can't receive more money!");

        verify(chatApi, never()).createTransferMessage(any(TransferMessageRequest.class));
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));

    }

    @Test
    void systemCreateTransferToUser() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException, NegativeAmountException, MaxAmountException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", 50000L, "Hello");

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId(request.getReceiverId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getReceiverId(), "users")).thenReturn(Optional.of(targetWallet));

        doNothing().when(walletService).transferMoney(sourceWallet.getId(), targetWallet.getId(), request.getAmount());

        // when
        underTest.systemCreateTransferToUser(system, request);

        // then
        verify(walletService, times(1)).transferMoney(sourceWallet.getId(), targetWallet.getId(), request.getAmount());
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));
    }

    @Test
    void systemCreateTransferToUserWillThrowNegativeAmountException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", -50000L, "Hello");

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferToUser(system, request))
                .isInstanceOf(NegativeAmountException.class)
                .hasMessageContaining("Negative amount");

        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void systemCreateTransferToUserWillThrowMaxAmountException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", 51_000_000L, "Hello");

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferToUser(system, request))
                .isInstanceOf(MaxAmountException.class)
                .hasMessageContaining("Transaction limit is " + MoneyConstant.MAX_AMOUNT);

        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void systemCreateTransferToUserWillThrowSourceHaveNoWalletException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", 50000L, "Hello");

        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId()))
                .thenReturn(Optional.empty());
        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferToUser(system, request))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Source has no wallet");

        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
    }

    @Test
    void systemCreateTransferToUserWillThrowTargetHaveNoWalletException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", 50000L, "Hello");

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();

        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId()))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getReceiverId(), "users"))
                .thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferToUser(system, request))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Target has no wallet");

        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
    }

    @Test
    void systemCreateTransferToUserWillThrowBalanceNotEnoughException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", 50000L, "Hello");

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId(request.getReceiverId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getReceiverId(), "users")).thenReturn(Optional.of(targetWallet));

        doThrow(new BalanceNotEnoughException()).when(walletService).transferMoney(sourceWallet.getId(), targetWallet.getId(), request.getAmount());

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferToUser(system, request))
                .isInstanceOf(BalanceNotEnoughException.class)
                .hasMessageContaining("Your balance is not enough");

        verify(walletService, times(1)).transferMoney(sourceWallet.getId(), targetWallet.getId(), request.getAmount());
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));
    }

    @Test
    void systemCreateTransferToUserWillThrowMaxBalanceException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferToUserRequest request =
                new SystemCreateTransferToUserRequest(1L, "uuid1", 50000L, "Hello");

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId(request.getReceiverId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId())).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByOwnerIdAndRefFrom(request.getReceiverId(), "users")).thenReturn(Optional.of(targetWallet));

        doThrow(new MaxBalanceException()).when(walletService).transferMoney(sourceWallet.getId(), targetWallet.getId(), request.getAmount());

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferToUser(system, request))
                .isInstanceOf(MaxBalanceException.class)
                .hasMessageContaining("Target can't receive more money!");

        verify(walletService, times(1)).transferMoney(sourceWallet.getId(), targetWallet.getId(), request.getAmount());
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));
    }

    @Test
    void systemCreateTransferFromUser() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException, NegativeAmountException, MaxAmountException, SoftTokenAuthorizeException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(request.getUserId())
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(request.getWalletId())
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId()))
                .thenReturn(Optional.of(targetWallet));
        doNothing().when(walletService).transferMoney(
                sourceWallet.getId(),
                targetWallet.getId(),
                softTokenEncoded.getAmount()
        );

        // when
        SystemCreateTransferFromUserResponse actual = underTest.systemCreateTransferFromUser(system, request);

        // then
        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));
        assertThat(actual.getAmount()).isEqualTo(50000L);

    }

    @Test
    void systemCreateTransferFromUserThrowSoftTokenAuthorizeException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(false, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(SoftTokenAuthorizeException.class);

        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());
    }

    @Test
    void systemCreateTransferFromUserThrowSoftTokenAuthorizeException2() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(SoftTokenAuthorizeException.class)
                .hasMessageContaining("Unauthorized!");

        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());
    }

    @Test
    void systemCreateTransferFromUserThrowSourceHaveNoWalletException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        when(walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.empty());
        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Source has no wallet");

        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());

    }


    @Test
    void systemCreateTransferFromUserThrowTargetHaveNoWalletException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(request.getUserId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId()))
                .thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Target has no wallet");

        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());

    }

    @Test
    void systemCreateTransferFromUserThrowNegativeAmountException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", -50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);
;
        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(NegativeAmountException.class)
                .hasMessageContaining("Negative amount");

        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());

    }

    @Test
    void systemCreateTransferFromUserThrowMaxAmountException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 51_000_000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);
        ;
        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(MaxAmountException.class)
                .hasMessageContaining("Transaction limit is " + MoneyConstant.MAX_AMOUNT);

        verify(transferStatementRepository, never()).save(any(TransferStatement.class));
        verify(walletService, never()).transferMoney(anyLong(), anyLong(), anyLong());

    }

    @Test
    void systemCreateTransferFromUserWillThrowBalanceNotEnoughException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(request.getUserId())
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(request.getWalletId())
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId()))
                .thenReturn(Optional.of(targetWallet));
        doThrow(new BalanceNotEnoughException()).when(walletService).transferMoney(
                sourceWallet.getId(),
                targetWallet.getId(),
                softTokenEncoded.getAmount()
        );

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(BalanceNotEnoughException.class)
                .hasMessageContaining("Your balance is not enough");

        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));
    }

    @Test
    void systemCreateTransferFromUserWillThrowMaxBalanceException() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        // given
        System system = System.builder()
                .id("uuid2")
                .build();
        SystemCreateTransferFromUserRequest request = new SystemCreateTransferFromUserRequest(
                "uuid1",
                2L,
                "softToken",
                "hello"
        );
        VerifySoftTokenResponse.SoftTokenEncoded softTokenEncoded = new VerifySoftTokenResponse.SoftTokenEncoded("uuid1", 50000L);
        VerifySoftTokenResponse authResponse = new VerifySoftTokenResponse(true, 200, "", softTokenEncoded);

        when(authApi.verifySoftToken(request.getSoftToken())).thenReturn(authResponse);

        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId(request.getUserId())
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(request.getWalletId())
                .ownerId(system.getId())
                .refFrom("systems")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(request.getUserId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findWalletByIdAndOwnerId(request.getWalletId(), system.getId()))
                .thenReturn(Optional.of(targetWallet));
        doThrow(new MaxBalanceException()).when(walletService).transferMoney(
                sourceWallet.getId(),
                targetWallet.getId(),
                softTokenEncoded.getAmount()
        );

        // when

        // then
        assertThatThrownBy(() -> underTest.systemCreateTransferFromUser(system, request))
                .isInstanceOf(MaxBalanceException.class)
                .hasMessageContaining("Target can't receive more money!");

        verify(transferStatementRepository, times(2)).save(any(TransferStatement.class));
    }

    @Test
    void topUp() throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException {
        // given
        User user = User.builder()
                .id("uuid")
                .build();
        TopUpRequest request = new TopUpRequest(50000L, "bankId");

        Wallet bankWallet = Wallet.builder()
                .id(1L)
                .ownerId(request.getBankId())
                .refFrom("systems")
                .balance(5000000L)
                .build();
        Wallet userWallet = Wallet.builder()
                .id(2L)
                .ownerId(user.getId())
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(request.getBankId(), OwnerWalletRefFrom.SYSTEMS))
                .thenReturn(Optional.of(bankWallet));
        when(walletRepository.findAndLockByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(userWallet));

        // when
        underTest.topUp(user, request);

        // then
        verify(transferStatementRepository, times(1)).save(any(TransferStatement.class));

    }

    @Test
    void topUpThrowMaxAmountException() {
        // given
        User user = User.builder()
                .id("uuid")
                .build();
        TopUpRequest request = new TopUpRequest(51_000_000L, "bankId");

        // when

        // then
        assertThatThrownBy(() -> underTest.topUp(user, request))
                .isInstanceOf(MaxAmountException.class)
                .hasMessageContaining("Transaction limit is " + MoneyConstant.MAX_AMOUNT);
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void topUpThrowMaxBalanceException() {
        // given
        User user = User.builder()
                .id("uuid")
                .build();
        TopUpRequest request = new TopUpRequest(50_000L, "bankId");

        Wallet bankWallet = Wallet.builder()
                .id(1L)
                .ownerId(request.getBankId())
                .refFrom("systems")
                .balance(5000000L)
                .build();
        Wallet userWallet = Wallet.builder()
                .id(2L)
                .ownerId(user.getId())
                .refFrom("users")
                .balance(49_999_999_000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom(request.getBankId(), OwnerWalletRefFrom.SYSTEMS))
                .thenReturn(Optional.of(bankWallet));
        when(walletRepository.findAndLockByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(userWallet));

        // when

        // then
        assertThatThrownBy(() -> underTest.topUp(user, request))
                .isInstanceOf(MaxBalanceException.class)
                .hasMessageContaining("Target can't receive more money!");
        verify(transferStatementRepository, never()).save(any(TransferStatement.class));

    }

    @Test
    void getTransferStatementOfUser() throws DatabaseHasErr, HaveNoWalletException, ApiErrException {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId("uuid2")
                .refFrom("users")
                .balance(5000000L)
                .build();

        TransferStatement transferStatement = TransferStatement.builder()
                .id(1L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .amount(50000L)
                .sourceId(1L)
                .targetId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        List<TransferStatement> transferStatements = Collections.singletonList(transferStatement);

        GetUserInfoResponse.UserInfo source = new GetUserInfoResponse.UserInfo(
                "uuid1",
                "ntan",
                "ntan@gmail.com",
                "Trinh An"
        );
        GetUserInfoResponse sourceResponse = GetUserInfoResponse.builder()
                .code(200)
                .message("")
                .payload(source)
                .success(true)
                .build();

        GetUserInfoResponse.UserInfo target = new GetUserInfoResponse.UserInfo(
                "uuid2",
                "nguyen",
                "nguyen@gmail.com",
                "Nguyen"
        );
        GetUserInfoResponse targetResponse = GetUserInfoResponse.builder()
                .code(200)
                .message("")
                .payload(target)
                .success(true)
                .build();

        TransferStatementDTO transferStatementDTO = TransferStatementDTO.builder()
                .amount(50000L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .createdAt(LocalDateTime.now())
                .source(source)
                .target(target)
                .build();
        List<TransferStatementDTO> transferStatementDTOs = Collections.singletonList(transferStatementDTO);

        when(walletRepository.findByOwnerIdAndRefFrom("uuid1", OwnerWalletRefFrom.USERS)).thenReturn(Optional.of(sourceWallet));
        when(transferStatementRepository.findAllBySourceIdOrTargetId(sourceWallet.getId(), PageRequest.of(0, 10))).thenReturn(transferStatements);

        when(transferStatementMapper.ts2TsDTO(transferStatement)).thenReturn(transferStatementDTO);

        when(walletRepository.findById(sourceWallet.getId())).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findById(targetWallet.getId())).thenReturn(Optional.of(targetWallet));

        when(authApi.getUserInfo(sourceWallet.getOwnerId())).thenReturn(sourceResponse);
        when(authApi.getUserInfo(targetWallet.getOwnerId())).thenReturn(targetResponse);

        // when
        List<TransferStatementDTO> actual = underTest.getTransferStatementOfUser("uuid1", 0, 10);

        // then
        assertThat(actual).isEqualTo(transferStatementDTOs);
    }

    @Test
    void getTransferStatementOfUserThrowApiErrException() {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000000L)
                .build();

        TransferStatement transferStatement = TransferStatement.builder()
                .id(1L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .amount(50000L)
                .sourceId(1L)
                .targetId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        List<TransferStatement> transferStatements = Collections.singletonList(transferStatement);

        GetUserInfoResponse.UserInfo source = new GetUserInfoResponse.UserInfo(
                "uuid1",
                "ntan",
                "ntan@gmail.com",
                "Trinh An"
        );
        GetUserInfoResponse sourceResponse = GetUserInfoResponse.builder()
                .code(200)
                .message("")
                .payload(source)
                .success(false)
                .build();

        GetUserInfoResponse.UserInfo target = new GetUserInfoResponse.UserInfo(
                "uuid2",
                "nguyen",
                "nguyen@gmail.com",
                "Nguyen"
        );

        TransferStatementDTO transferStatementDTO = TransferStatementDTO.builder()
                .amount(50000L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .createdAt(LocalDateTime.now())
                .source(source)
                .target(target)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom("uuid1", OwnerWalletRefFrom.USERS)).thenReturn(Optional.of(sourceWallet));
        when(transferStatementRepository.findAllBySourceIdOrTargetId(sourceWallet.getId(), PageRequest.of(0, 10))).thenReturn(transferStatements);

        when(transferStatementMapper.ts2TsDTO(transferStatement)).thenReturn(transferStatementDTO);

        when(walletRepository.findById(sourceWallet.getId())).thenReturn(Optional.of(sourceWallet));

        when(authApi.getUserInfo(sourceWallet.getOwnerId())).thenReturn(sourceResponse);

        // when

        // then
        assertThatThrownBy(() -> underTest.getTransferStatementOfUser("uuid1", 0, 10))
                .isInstanceOf(ApiErrException.class);
    }

    @Test
    void getTransferStatementOfUserWithSystemThrowApiErrException() {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId("uuid2")
                .refFrom("systems")
                .balance(5000000L)
                .build();

        TransferStatement transferStatement = TransferStatement.builder()
                .id(1L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .amount(50000L)
                .sourceId(1L)
                .targetId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        List<TransferStatement> transferStatements = Collections.singletonList(transferStatement);

        GetUserInfoResponse.UserInfo source = new GetUserInfoResponse.UserInfo(
                "uuid1",
                "ntan",
                "ntan@gmail.com",
                "Trinh An"
        );
        GetUserInfoResponse sourceResponse = GetUserInfoResponse.builder()
                .code(200)
                .message("")
                .payload(source)
                .success(true)
                .build();


        GetSystemInfoResponse.SystemInfo target = new GetSystemInfoResponse.SystemInfo(
                "uuid2",
                "lucky"
        );
        GetSystemInfoResponse targetResponse = GetSystemInfoResponse.builder()
                .code(200)
                .message("")
                .payload(target)
                .success(false)
                .build();

        TransferStatementDTO transferStatementDTO = TransferStatementDTO.builder()
                .amount(50000L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .createdAt(LocalDateTime.now())
                .source(source)
                .target(target)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom("uuid1", OwnerWalletRefFrom.USERS)).thenReturn(Optional.of(sourceWallet));
        when(transferStatementRepository.findAllBySourceIdOrTargetId(sourceWallet.getId(), PageRequest.of(0, 10))).thenReturn(transferStatements);

        when(transferStatementMapper.ts2TsDTO(transferStatement)).thenReturn(transferStatementDTO);

        when(walletRepository.findById(sourceWallet.getId())).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findById(targetWallet.getId())).thenReturn(Optional.of(targetWallet));

        when(authApi.getUserInfo(sourceWallet.getOwnerId())).thenReturn(sourceResponse);
        when(authApi.getSystemInfo(targetWallet.getOwnerId())).thenReturn(targetResponse);

        // when

        // then
        assertThatThrownBy(() -> underTest.getTransferStatementOfUser("uuid1", 0, 10))
                .isInstanceOf(ApiErrException.class);
    }

    @Test
    void getTransferStatementOfUserWithSystem() throws DatabaseHasErr, HaveNoWalletException, ApiErrException {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId("uuid2")
                .refFrom("systems")
                .balance(5000000L)
                .build();

        TransferStatement transferStatement = TransferStatement.builder()
                .id(1L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .amount(50000L)
                .sourceId(1L)
                .targetId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        List<TransferStatement> transferStatements = Collections.singletonList(transferStatement);

        GetUserInfoResponse.UserInfo source = new GetUserInfoResponse.UserInfo(
                "uuid1",
                "ntan",
                "ntan@gmail.com",
                "Trinh An"
        );
        GetUserInfoResponse sourceResponse = GetUserInfoResponse.builder()
                .code(200)
                .message("")
                .payload(source)
                .success(true)
                .build();


        GetSystemInfoResponse.SystemInfo target = new GetSystemInfoResponse.SystemInfo(
                "uuid2",
                "lucky"
        );
        GetSystemInfoResponse targetResponse = GetSystemInfoResponse.builder()
                .code(200)
                .message("")
                .payload(target)
                .success(true)
                .build();

        TransferStatementDTO transferStatementDTO = TransferStatementDTO.builder()
                .amount(50000L)
                .status(TransferStatus.SUCCESS)
                .transferFee(0L)
                .createdAt(LocalDateTime.now())
                .source(source)
                .target(target)
                .build();
        List<TransferStatementDTO> transferStatementDTOs = Collections.singletonList(transferStatementDTO);

        when(walletRepository.findByOwnerIdAndRefFrom("uuid1", OwnerWalletRefFrom.USERS)).thenReturn(Optional.of(sourceWallet));
        when(transferStatementRepository.findAllBySourceIdOrTargetId(sourceWallet.getId(), PageRequest.of(0, 10))).thenReturn(transferStatements);

        when(transferStatementMapper.ts2TsDTO(transferStatement)).thenReturn(transferStatementDTO);

        when(walletRepository.findById(sourceWallet.getId())).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findById(targetWallet.getId())).thenReturn(Optional.of(targetWallet));

        when(authApi.getUserInfo(sourceWallet.getOwnerId())).thenReturn(sourceResponse);
        when(authApi.getSystemInfo(targetWallet.getOwnerId())).thenReturn(targetResponse);

        // when
        List<TransferStatementDTO> actual = underTest.getTransferStatementOfUser("uuid1", 0, 10);

        // then
        assertThat(actual).isEqualTo(transferStatementDTOs);
    }
}