package com.hey.payment.service;

import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.dto.auth_system.GetSystemsResponse;
import com.hey.payment.dto.system.SystemDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;
import com.hey.payment.entity.Wallet;
import com.hey.payment.exception_handler.exception.BalanceNotEnoughException;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.exception_handler.exception.MaxBalanceException;
import com.hey.payment.mapper.WalletMapper;
import com.hey.payment.repository.WalletRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {
    @InjectMocks
    private WalletServiceImpl underTest;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WalletRepository walletRepository;

    @Test
    void getSystems() {
        GetSystemsResponse systemsResponse =
                new GetSystemsResponse(
                        true,
                        200,
                        "",
                        Collections.singletonList(
                                new SystemDTO("uuid", "lucky", 10)
                        )
                );
        when(restTemplate.getForObject("/getSystems", GetSystemsResponse.class)).thenReturn(systemsResponse);

        when(walletRepository.countAllByOwnerIdAndRefFrom("uuid", OwnerWalletRefFrom.SYSTEMS)).thenReturn(0L);

        // when
        underTest.getSystems();

        // then
        verify(walletRepository, times(1)).saveAll(any(Iterable.class));
    }

    @Test
    void transferMoney() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
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

        when(walletRepository.findAndLockWalletById(1L)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findAndLockWalletById(2L)).thenReturn(Optional.of(targetWallet));

        // when
        underTest.transferMoney(1L, 2L, 50000L);

        // then
        verify(walletRepository, times(2)).save(any(Wallet.class));

    }

    @Test
    void transferMoneyThrowSourceHaveNoWallet() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        when(walletRepository.findAndLockWalletById(1L)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.transferMoney(1L, 2L, 50000L))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Source has no wallet");
        verify(walletRepository, never()).save(any(Wallet.class));

    }

    @Test
    void transferMoneyThrowTargetHaveNoWallet() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000000L)
                .build();

        when(walletRepository.findAndLockWalletById(1L)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findAndLockWalletById(2L)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.transferMoney(1L, 2L, 50000L))
                .isInstanceOf(HaveNoWalletException.class)
                .hasMessageContaining("Target has no wallet");
        verify(walletRepository, never()).save(any(Wallet.class));

    }

    @Test
    void transferMoneyThrowMaxBalance() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
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
                .balance(50_000_000_000L)
                .build();

        when(walletRepository.findAndLockWalletById(1L)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findAndLockWalletById(2L)).thenReturn(Optional.of(targetWallet));

        // when

        // then
        assertThatThrownBy(() -> underTest.transferMoney(1L, 2L, 50000L))
                .isInstanceOf(MaxBalanceException.class);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void transferMoneyThrowBalanceNotEnough() throws BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000L)
                .build();
        Wallet targetWallet = Wallet.builder()
                .id(2L)
                .ownerId("uuid2")
                .refFrom("users")
                .balance(50_000L)
                .build();

        when(walletRepository.findAndLockWalletById(1L)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findAndLockWalletById(2L)).thenReturn(Optional.of(targetWallet));

        // when

        // then
        assertThatThrownBy(() -> underTest.transferMoney(1L, 2L, 50000L))
                .isInstanceOf(BalanceNotEnoughException.class);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @SneakyThrows
    @Test
    void findWalletOfUser() {
        Wallet sourceWallet = Wallet.builder()
                .id(1L)
                .ownerId("uuid1")
                .refFrom("users")
                .balance(5000L)
                .build();

        when(walletRepository.findByOwnerIdAndRefFrom("uuid1", OwnerWalletRefFrom.USERS))
                .thenReturn(Optional.of(sourceWallet));

        // when
        underTest.findWalletOfUser("uuid1");

        // then
        verify(walletMapper, times(1)).wallet2WalletDTO(sourceWallet);
    }

    @Test
    void findAllWalletsOfSystem() {
        System system = System.builder().id("uuid").build();

        // when
        underTest.findAllWalletsOfSystem(system);

        // then
        verify(walletRepository, times(1)).findAllByOwnerIdAndRefFrom(system.getId(), OwnerWalletRefFrom.SYSTEMS);
    }

    @SneakyThrows
    @Test
    void createWallet() {
        User user = User.builder().id("uuid").build();

        when(walletRepository.existsByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS))
                .thenReturn(false);

        // when
        underTest.createWallet(user);

        // then
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(walletMapper, times(1)).wallet2WalletDTO(any(Wallet.class));

    }
}