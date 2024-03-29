package com.hey.integration.repository;

import com.hey.integration.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT SUM(wallet.balance) FROM Wallet wallet")
    Long sumAllBalance();

    @Query
    void deleteWalletsByRefFrom(String refFrom);
    int countAllByBalanceLessThan(Long balance);
}
