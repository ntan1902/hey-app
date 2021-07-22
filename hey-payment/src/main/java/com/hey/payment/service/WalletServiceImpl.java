package com.hey.payment.service;

import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.mapper.WalletMapper;
import com.hey.payment.repository.WalletRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
        return walletRepository.findByOwnerId(userId)
                .map(walletMapper::wallet2WalletDTO)
                .orElseThrow(() ->{
                    log.error("User {} have no wallet",userId);
                    throw new HaveNoWalletException();
                });
    }

}
