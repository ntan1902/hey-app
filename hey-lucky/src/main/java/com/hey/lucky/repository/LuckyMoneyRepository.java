package com.hey.lucky.repository;

import com.hey.lucky.entity.LuckyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LuckyMoneyRepository extends JpaRepository<LuckyMoney, Long> {
}
