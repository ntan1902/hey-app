package com.hey.payment.service;


import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.HasWalletResponse;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.System;
import com.hey.payment.entity.User;
import com.hey.payment.entity.Wallet;
import com.hey.payment.exception_handler.exception.*;

import javax.persistence.LockModeType;
import java.util.List;

public interface WalletService {
    WalletDTO findWalletOfUser(String userId) throws HaveNoWalletException;

    List<WalletSystemDTO> findAllWalletsOfSystem(System system);

    WalletDTO createWallet(User user) throws HadWalletException;

    void getSystems();

    void transferMoney(long sourceWalletId, long targetWalletId, long amount) throws MaxBalanceException, BalanceNotEnoughException, HaveNoWalletException;

    HasWalletResponse hasWallet(User user);
}
