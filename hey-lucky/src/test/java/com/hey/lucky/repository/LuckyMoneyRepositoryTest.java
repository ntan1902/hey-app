package com.hey.lucky.repository;

import com.hey.lucky.entity.LuckyMoney;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class LuckyMoneyRepositoryTest {

    @Autowired
    private LuckyMoneyRepository luckyMoneyRepository;


    @Test
    void getLuckyMoneyById(){
        // given
        LuckyMoney expect = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1L)
                .sessionChatId("123abc")
                .amount(50000L)
                .restMoney(50000L)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(expect);

        // when
        LuckyMoney actual = luckyMoneyRepository.getLuckyMoneyById(expect.getId()).get();
        // then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void findLuckyMoneyById() {
        // given
        LuckyMoney expect = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(expect);

        // when
        LuckyMoney actual = luckyMoneyRepository.findLuckyMoneyById(expect.getId()).get();
        // then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void findAllBySessionChatId() {
        // given
        LuckyMoney lm = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm);
        LuckyMoney lm1 = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm1);
        LuckyMoney lm2 = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm2);
        LuckyMoney lm3 = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("1234abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm3);
        List<LuckyMoney> expectList = new ArrayList<>();
        expectList.add(lm);
        expectList.add(lm1);
        expectList.add(lm2);



        // when
        List<LuckyMoney> actualList = luckyMoneyRepository.findAllBySessionChatId("123abc");

        // then
        assertThat(actualList).isEqualTo(expectList);

    }

    @Test
    void getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween() {
        // given
        LuckyMoney lm = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm);
        LuckyMoney lm1 = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(0l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm1);
        LuckyMoney lm2 = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,10,19,20))
                .build();
        luckyMoneyRepository.save(lm2);
        LuckyMoney lm3 = LuckyMoney.builder()
                .userId("abc")
                .systemWalletId(1l)
                .sessionChatId("1234abc")
                .amount(50000l)
                .restMoney(50000l)
                .numberBag(10)
                .restBag(10)
                .type("random")
                .wishMessage("hello")
                .createdAt(LocalDateTime.of(2021,8,1,10,19,20))
                .expiredAt(LocalDateTime.of(2021,8,2,12,19,20))
                .build();
        luckyMoneyRepository.save(lm3);
        List<LuckyMoney> expectList = new ArrayList<>();
        expectList.add(lm);
        expectList.add(lm2);



        // when
        LocalDateTime start = LocalDateTime.of(2021,8,2,10,19,10);
        LocalDateTime end = LocalDateTime.of(2021,8,2,10,19,20);
        List<LuckyMoney> actualList = luckyMoneyRepository.getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween(start,end);

        // then
        assertThat(actualList).isEqualTo(expectList);
    }


}