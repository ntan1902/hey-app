package com.hey.payment.repository;

import com.hey.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByOwnerId(Long userId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> getByOwnerId(Long userId);

    boolean existsByOwnerId(Long userId);
}
