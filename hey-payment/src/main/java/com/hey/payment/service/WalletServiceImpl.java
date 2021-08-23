package com.hey.payment.service;

import com.hey.payment.api.AuthApi;
import com.hey.payment.constant.MoneyConstant;
import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.dto.auth_system.GetSystemsResponse;
import com.hey.payment.dto.system.SystemDTO;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.HasWalletResponse;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;
import com.hey.payment.entity.Wallet;
import com.hey.payment.exception_handler.exception.BalanceNotEnoughException;
import com.hey.payment.exception_handler.exception.HadWalletException;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.exception_handler.exception.MaxBalanceException;
import com.hey.payment.mapper.WalletMapper;
import com.hey.payment.repository.WalletRepository;
import com.hey.payment.utils.SystemUtil;
import com.hey.payment.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    private final AuthApi authApi;

    private final SystemUtil systemUtil;

    private final UserUtil userUtil;

    @EventListener(ApplicationReadyEvent.class)
    public void getSystems() {
        log.info("Inside getSystems of WalletServiceImpl");
        GetSystemsResponse response = authApi.getSystems();
        log.info("{}", response);

        if (response != null) {
            List<SystemDTO> systems = response.getPayload();

            List<Wallet> wallets = new LinkedList<>();
            systems.forEach(system -> {
                if (system.getNumberOfWallet() != 0) {
                    long numberWalletNow = walletRepository.countAllByOwnerIdAndRefFrom(system.getId(),
                            OwnerWalletRefFrom.SYSTEMS);
                    for (int i = 0; i < system.getNumberOfWallet() - numberWalletNow; ++i) {
                        wallets.add(Wallet.builder().ownerId(system.getId()).balance(0L)
                                .refFrom(OwnerWalletRefFrom.SYSTEMS).build());
                    }
                }
            });

            walletRepository.saveAll(wallets);
        }

    }

    @Override
    @Transactional(rollbackFor = {BalanceNotEnoughException.class})
    public void transferMoney(long sourceWalletId, long targetWalletId, long amount) throws MaxBalanceException, BalanceNotEnoughException, HaveNoWalletException {
        // Get and lock 2 wallets
        Wallet sourceWallet = walletRepository.findAndLockWalletById(sourceWalletId)
                .orElseThrow(() -> new HaveNoWalletException("Source has no wallet"));

        Wallet targetWallet = walletRepository.findAndLockWalletById(targetWalletId)
                .orElseThrow(() -> new HaveNoWalletException("Target has no wallet"));
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
    public WalletDTO findWalletOfUser() throws HaveNoWalletException {
        User user = userUtil.getCurrentUser();
        log.info("Inside findWalletOfUser of WalletServiceImpl with id {}", user.getId());
        Wallet wallet = walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElseThrow(() -> new HaveNoWalletException("User has no wallet"));
        return walletMapper.wallet2WalletDTO(wallet);
    }

    @Override
    public List<WalletSystemDTO> findAllWalletsOfSystem() {
        System system = systemUtil.getCurrentSystem();
        log.info("Inside findAllWalletsOfSystem of WalletServiceImpl: {}", system.getId());
        return walletRepository.findAllByOwnerIdAndRefFrom(system.getId(), OwnerWalletRefFrom.SYSTEMS).stream()
                .map(walletMapper::wallet2WalletSystemDTO).collect(Collectors.toList());
    }

    @Override
    public WalletDTO createWallet() throws HadWalletException {
        User user = userUtil.getCurrentUser();
        log.info("Inside createWallet of WalletServiceImpl: {}", user);
        if (walletRepository.existsByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)) {
            throw new HadWalletException();
        }
        Wallet wallet = Wallet.builder()
                .ownerId(user.getId())
                .balance(0L)
                .refFrom(OwnerWalletRefFrom.USERS)
                .build();
        walletRepository.save(wallet);

        return walletMapper.wallet2WalletDTO(wallet);
    }

    @Override
    public HasWalletResponse hasWallet() {
        User user = userUtil.getCurrentUser();
        log.info("Inside hasWallet of WalletServiceImpl with user {}", user);
        Wallet wallet = walletRepository.findByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)
                .orElse(null);
        return new HasWalletResponse(wallet != null);
    }

    public boolean isMaxBalance(long balance) {
        return balance > MoneyConstant.MAX_BALANCE;
    }
}
