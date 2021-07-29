package com.hey.lucky.repository;

import com.hey.lucky.entity.LuckyMoney;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LuckyMoneyRepositoryTest {
    @Autowired
    private LuckyMoneyRepository luckyMoneyRepository;

    @Test
    void getLuckyMoneyById(){
        // given
        LuckyMoney expect = LuckyMoney.builder()
                .id(1l)
                .userId(1l)
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
        LuckyMoney actual = luckyMoneyRepository.getLuckyMoneyById(1l).get();
        // then
        assertThat(actual).isEqualTo(expect);
    }


}
