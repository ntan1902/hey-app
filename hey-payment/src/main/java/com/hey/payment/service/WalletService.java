package com.hey.payment.service;


import com.hey.payment.dto.user.WalletDTO;

public interface WalletService {
    public WalletDTO getWalletOfUser(long userId);
}
