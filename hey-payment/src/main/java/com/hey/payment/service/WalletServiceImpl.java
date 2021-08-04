package com.hey.payment.service;

import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.dto.auth_system.GetSystemsResponse;
import com.hey.payment.dto.system.SystemDTO;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;
import com.hey.payment.entity.Wallet;
import com.hey.payment.exception_handler.exception.HadWalletException;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.mapper.WalletMapper;
import com.hey.payment.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    private final RestTemplate restTemplate;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper, RestTemplate restTemplate) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
        this.restTemplate = restTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getSystems() {
        log.info("Inside getSystems of WalletServiceImpl");
        GetSystemsResponse response = restTemplate.getForObject("/getSystems", GetSystemsResponse.class);
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
    public WalletDTO getWalletOfUser(String userId) throws HaveNoWalletException {
        log.info("Inside getWalletOfUser of WalletServiceImpl with id {}", userId);
        Wallet wallet = walletRepository.findByOwnerIdAndRefFrom(userId, OwnerWalletRefFrom.USERS)
                .orElseThrow(HaveNoWalletException::new);
        return walletMapper.wallet2WalletDTO(wallet);
    }

    @Override
    public List<WalletSystemDTO> getAllWalletsOfSystem(System system) {
        log.info("Inside getAllWalletOfSystem of WalletServiceImpl: {}", system.getId());
        return walletRepository.findAllByOwnerIdAndRefFrom(system.getId(), OwnerWalletRefFrom.SYSTEMS).stream()
                .map(walletMapper::wallet2WalletSystemDTO).collect(Collectors.toList());
    }

    @Override
    public WalletDTO createWallet(User user) throws HadWalletException {
        log.info("Inside createWallet of WalletServiceImpl: {}", user);
        if (walletRepository.existsByOwnerIdAndRefFrom(user.getId(), OwnerWalletRefFrom.USERS)) {
            throw new HadWalletException();
        }
        Wallet wallet = Wallet.builder().ownerId(user.getId()).balance(0L).refFrom(OwnerWalletRefFrom.USERS).build();
        walletRepository.save(wallet);

        return walletMapper.wallet2WalletDTO(wallet);
    }

}
