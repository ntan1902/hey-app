package com.hey.payment.repository;

import com.hey.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByOwnerIdAndRefFrom(String userId, String refFrom);

    List<Wallet> findAllByOwnerIdAndRefFrom(String systemId, String refFrom);

    Optional<Wallet> findWalletByIdAndOwnerId(Long walletId, String ownerId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> getByOwnerIdAndRefFrom(String userId, String refFrom);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
//    @Query(value = "SELECT * FROM wallets w WHERE w.id=?1 FOR UPDATE", nativeQuery = true)
    Optional<Wallet> getWalletById(Long walletId);

    boolean existsByOwnerIdAndRefFrom(String userId, String refFrom);

    long countAllByOwnerIdAndRefFrom(String ownerId, String refFrom);

}
