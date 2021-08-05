package com.hey.lucky.repository;

import com.hey.lucky.entity.LuckyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LuckyMoneyRepository extends JpaRepository<LuckyMoney, Long> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<LuckyMoney> getLuckyMoneyById(Long luckyMoneyId);
    
    Optional<LuckyMoney> findLuckyMoneyById(Long luckyMoneyId);
    
    List<LuckyMoney> findAllBySessionChatId(String sessionChatId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select lm from LuckyMoney lm where lm.restMoney > 0 and lm.expiredAt >= :start and lm.expiredAt <= :end ")
    List<LuckyMoney> getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween(LocalDateTime start, LocalDateTime end);
}
