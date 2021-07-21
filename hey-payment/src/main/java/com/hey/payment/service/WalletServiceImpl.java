package com.hey.payment.service;

import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.Wallet;
import com.hey.payment.mapper.WalletMapper;
import com.hey.payment.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public WalletDTO getWalletOfUser(long userId) {
        Wallet wallet = walletRepository.findByOwner(userId);
        WalletDTO walletDTO = walletMapper.wallet2WalletDTO(wallet);
        return walletDTO;
    }

}
