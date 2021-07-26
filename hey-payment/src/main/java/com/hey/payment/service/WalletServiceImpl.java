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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    private final RestTemplate restTemplate;

    @PostConstruct
    public void getSystems() {
        log.info("Inside getSystems of WalletServiceImpl");
        GetSystemsResponse response = restTemplate.getForObject(
                "http://localhost:7070" + "/api/v1/systems/getSystems",
                GetSystemsResponse.class
        );
        log.info("{}", response);

        if (response != null) {
            List<SystemDTO> systems = response.getPayload();

            List<Wallet> wallets = new LinkedList<>();
            systems.forEach(system -> {
                if (system.getNumberOfWallet() != 0) {
                    for (int i = 0; i < system.getNumberOfWallet(); ++i) {
                        wallets.add(
                                Wallet.builder()
                                        .ownerId(system.getId())
                                        .balance(0L)
                                        .refFrom(OwnerWalletRefFrom.SYSTEMS)
                                        .build()
                        );
                    }
                }
            });

            walletRepository.saveAll(wallets);
        }

    }

    @Override
    public WalletDTO getWalletOfUser(long userId) {
        log.info("Inside getWalletOfUser of WalletServiceImpl with id {}", userId);
        return walletRepository.findByOwnerIdAndRefFrom(userId, OwnerWalletRefFrom.USERS)
                .map(walletMapper::wallet2WalletDTO)
                .orElseThrow(() -> {
                    log.error("User {} have no wallet", userId);
                    throw new HaveNoWalletException();
                });
    }

    @Override
    public List<WalletSystemDTO> getAllWalletsOfSystem(System system) {
        log.info("Inside getAllWalletOfSystem of WalletServiceImpl: {}", system.getId());
        return walletRepository.findAllByOwnerIdAndRefFrom(system.getId(), OwnerWalletRefFrom.SYSTEMS)
                .stream().map(walletMapper::wallet2WalletSystemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WalletDTO createWallet(User user) {
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

}
