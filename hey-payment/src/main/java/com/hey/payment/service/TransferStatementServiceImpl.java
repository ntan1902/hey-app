package com.hey.payment.service;

import com.hey.payment.dto.user.CreateTransferRequest;
import com.hey.payment.dto.user.CreateTransferResponse;
import com.hey.payment.entity.TransferStatement;
import com.hey.payment.entity.User;
import com.hey.payment.entity.Wallet;
import com.hey.payment.exception_handler.exception.BalanceNotEnoughException;
import com.hey.payment.exception_handler.exception.HaveNoWalletException;
import com.hey.payment.exception_handler.exception.TargetWalletNotExistException;
import com.hey.payment.repository.TransferStatementRepository;
import com.hey.payment.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferStatementServiceImpl implements TransferStatementService {
    private final TransferStatementRepository transferStatementRepository;

    private final WalletRepository walletRepository;


    public TransferStatementServiceImpl(TransferStatementRepository transferStatementRepository, WalletRepository walletRepository) {
        this.transferStatementRepository = transferStatementRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public CreateTransferResponse createTransfer(User user, CreateTransferRequest createTransferRequest) {
        TransferStatement transferStatement = new TransferStatement();

        // 1. Kiểm tra user1 và user2 có wallet hay không
        Wallet s = walletRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> {
                    throw new HaveNoWalletException();
                });
        Wallet t = walletRepository.findByOwnerId(createTransferRequest.getTargetId())
                .orElseThrow(() -> {
                    throw new TargetWalletNotExistException();
                });

        // 2. Tạo transfer statement
        transferStatement.setSourceId(s.getId());
        transferStatement.setTargetId(t.getId());
        transferStatement.setAmount(createTransferRequest.getAmount());
        transferStatement.setStatus(1);
        transferStatement.setTransferFee(createTransferRequest.getAmount());

        transferStatementRepository.save(transferStatement);

//        ->> message Queue
//        return CreateTransferResponse

        Wallet sourceWallet = walletRepository.getByOwnerId(user.getId()).get();
        Wallet targetWallet = walletRepository.getByOwnerId(createTransferRequest.getTargetId()).get();

        long sourceBalance = sourceWallet.getBalance();
        long targetBalance = targetWallet.getBalance();
        long amount = createTransferRequest.getAmount();

        if (sourceBalance < amount) {
            sourceWallet.setBalance(sourceBalance - amount);
            targetWallet.setBalance(targetBalance + amount);
        }
        else {
            throw new BalanceNotEnoughException();
        }


        return new CreateTransferResponse();
    }
}
