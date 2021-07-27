package com.hey.lucky.repository;

import com.hey.lucky.entity.ReceivedLuckyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivedLuckyMoneyRepository extends JpaRepository<ReceivedLuckyMoney, Long> {
    boolean existsByLuckyMoneyIdAndReceiverId(Long luckyMoneyId, Long receiverId);
    ReceivedLuckyMoney findByLuckyMoneyIdAndReceiverId(Long luckyMoneyId, Long receiverId);
    List<ReceivedLuckyMoney> findAllByLuckyMoneyId(Long luckyMoneyId);
}
