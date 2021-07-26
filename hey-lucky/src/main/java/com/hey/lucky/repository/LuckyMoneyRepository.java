package com.hey.lucky.repository;

import com.hey.lucky.entity.LuckyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface LuckyMoneyRepository extends JpaRepository<LuckyMoney, Long> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<LuckyMoney> getLuckyMoneyById(Long luckyMoneyId);

    List<LuckyMoney> findAllBySessionChatId(String sessionChatId);
}
