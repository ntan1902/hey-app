package com.hey.payment.service;


import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.HasWalletResponse;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.exception_handler.exception.BalanceNotEnoughException;
import com.hey.payment.exception_handler.exception.HadWalletException;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.exception_handler.exception.MaxBalanceException;

import java.util.List;

public interface WalletService {
    WalletDTO findWalletOfUser() throws HaveNoWalletException;

    List<WalletSystemDTO> findAllWalletsOfSystem();

    WalletDTO createWallet() throws HadWalletException;

    void getSystems();

    void transferMoney(long sourceWalletId, long targetWalletId, long amount) throws MaxBalanceException, BalanceNotEnoughException, HaveNoWalletException;

    HasWalletResponse hasWallet();
}
