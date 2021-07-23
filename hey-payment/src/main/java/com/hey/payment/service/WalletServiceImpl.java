package com.hey.payment.service;

import com.hey.payment.constant.OwnerWalletRefFrom;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.System;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.mapper.WalletMapper;
import com.hey.payment.repository.WalletRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public WalletDTO getWalletOfUser(long userId) {
        log.info("Get wallet of user with id {}", userId);
        return walletRepository.findByOwnerIdAndRefFrom(userId, OwnerWalletRefFrom.USERS)
                .map(walletMapper::wallet2WalletDTO)
                .orElseThrow(() ->{
                    log.error("User {} have no wallet",userId);
                    throw new HaveNoWalletException();
                });
    }

    @Override
    public List<WalletSystemDTO> getAllWalletOfSystem(System system) {
        log.info("Get all wallet of system {}", system.getId());
        return walletRepository.findAllByOwnerIdAndRefFrom(system.getId(), OwnerWalletRefFrom.SYSTEMS)
                .stream().map(walletMapper::wallet2WalletSystemDTO)
                .collect(Collectors.toList());
    }

}
