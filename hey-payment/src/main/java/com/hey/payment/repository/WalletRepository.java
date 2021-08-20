package com.hey.payment.repository;

import com.hey.payment.entity.Wallet;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Cacheable(value = "wallets", key = "#id")
    Optional<Wallet> findById(Long id);

    @CachePut(value = "wallets", key = "#p0.id")
    Wallet save(Wallet wallet);

    Optional<Wallet> findByOwnerIdAndRefFrom(String userId, String refFrom);

    List<Wallet> findAllByOwnerIdAndRefFrom(String systemId, String refFrom);

    Optional<Wallet> findWalletByIdAndOwnerId(Long walletId, String ownerId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findAndLockByOwnerIdAndRefFrom(String userId, String refFrom);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findAndLockWalletById(Long walletId);

    boolean existsByOwnerIdAndRefFrom(String userId, String refFrom);

    long countAllByOwnerIdAndRefFrom(String ownerId, String refFrom);

}
