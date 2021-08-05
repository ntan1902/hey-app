package com.hey.lucky.repository;

import com.hey.lucky.entity.ReceivedLuckyMoney;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReceivedLuckyMoneyRepositoryTest {

    @Autowired
    private  ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;



    @Test
    void existsByLuckyMoneyIdAndReceiverIdReturnTrue() {
        // given
        ReceivedLuckyMoney rlm1 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(1L)
                .amount(2000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        receivedLuckyMoneyRepository.save(rlm1);

        // when
        boolean isExist = receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(1L,"abc");

        // then
        assertTrue(isExist);
    }

    @Test
    void existsByLuckyMoneyIdAndReceiverIdReturnFalse() {
        // given

        // when
        boolean isExist = receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(1L,"abc");

        // then
        assertFalse(isExist);
    }

    @Test
    void findByLuckyMoneyIdAndReceiverId() {
        // given
        ReceivedLuckyMoney expect = ReceivedLuckyMoney.builder()
                .luckyMoneyId(1L)
                .amount(2000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        receivedLuckyMoneyRepository.save(expect);

        // when
        ReceivedLuckyMoney actual = receivedLuckyMoneyRepository.findByLuckyMoneyIdAndReceiverId(1L,"abc");

        // then
        assertEquals(expect, actual);
    }
    @Test
    void findByLuckyMoneyIdAndReceiverIdReturnNull() {
        // given

        // when
        ReceivedLuckyMoney actual = receivedLuckyMoneyRepository.findByLuckyMoneyIdAndReceiverId(2L,"abc");

        // then
        assertNull(actual);
    }
    @Test
    void findAllByLuckyMoneyId() {
        // given
        ReceivedLuckyMoney rlm1 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(1L)
                .amount(2000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        ReceivedLuckyMoney rlm2 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(1L)
                .amount(2000L)
                .receiverId("def")
                .createdAt(LocalDateTime.now()).build();
        ReceivedLuckyMoney rlm3 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(3L)
                .amount(4000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        ReceivedLuckyMoney rlm4= ReceivedLuckyMoney.builder()
                .luckyMoneyId(4L)
                .amount(5000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        List<ReceivedLuckyMoney> expect = new ArrayList<>();
        expect.add(rlm1);
        expect.add(rlm2);

        receivedLuckyMoneyRepository.save(rlm1);
        receivedLuckyMoneyRepository.save(rlm2);
        receivedLuckyMoneyRepository.save(rlm3);
        receivedLuckyMoneyRepository.save(rlm4);
        // when
        List<ReceivedLuckyMoney> actual = receivedLuckyMoneyRepository.findAllByLuckyMoneyId(1L);

        assertEquals(expect,actual);

    }
    @Test
    void findAllByLuckyMoneyIdReturnArrayNull() {
        // given
        ReceivedLuckyMoney rlm1 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(1L)
                .amount(2000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        ReceivedLuckyMoney rlm2 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(1L)
                .amount(2000L)
                .receiverId("def")
                .createdAt(LocalDateTime.now()).build();
        ReceivedLuckyMoney rlm3 = ReceivedLuckyMoney.builder()
                .luckyMoneyId(3L)
                .amount(4000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        ReceivedLuckyMoney rlm4= ReceivedLuckyMoney.builder()
                .luckyMoneyId(4L)
                .amount(5000L)
                .receiverId("abc")
                .createdAt(LocalDateTime.now()).build();
        List<ReceivedLuckyMoney> expect = new ArrayList<>();

        receivedLuckyMoneyRepository.save(rlm1);
        receivedLuckyMoneyRepository.save(rlm2);
        receivedLuckyMoneyRepository.save(rlm3);
        receivedLuckyMoneyRepository.save(rlm4);
        // when
        List<ReceivedLuckyMoney> actual = receivedLuckyMoneyRepository.findAllByLuckyMoneyId(10L);

        assertEquals(expect,actual);

    }
}