package com.hey.payment.service;


import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;

import java.util.List;

public interface WalletService {
    WalletDTO getWalletOfUser(long userId);

    List<WalletSystemDTO> getAllWalletOfSystem(System system);

    WalletDTO createWallet(User user);
}
