package com.hey.payment.repository;

import com.hey.payment.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WalletRepositoryTest {

    @Autowired
    private WalletRepository underTest;

    @Test
    void findByOwnerIdAndRefFrom() {
        // given
        Wallet expected = Wallet.builder()
                .id(1L)
                .balance(50000L)
                .refFrom("users")
                .ownerId("uuid")
                .build();
        underTest.save(expected);

        // when
        Wallet actual = underTest.findByOwnerIdAndRefFrom("uuid", "users").get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllByOwnerIdAndRefFrom() {
        // given
        List<Wallet> expected = Arrays.asList(
                Wallet.builder()
                        .id(1L)
                        .balance(50000L)
                        .refFrom("systems")
                        .ownerId("uuid")
                        .build(),
                Wallet.builder()
                        .id(2L)
                        .balance(50000L)
                        .refFrom("systems")
                        .ownerId("uuid")
                        .build()
        );
        underTest.saveAll(expected);

        // when
        List<Wallet> actual = underTest.findAllByOwnerIdAndRefFrom("uuid", "systems");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findWalletByIdAndOwnerId() {
        // given
        Wallet expected = Wallet.builder()
                .id(1L)
                .balance(50000L)
                .refFrom("users")
                .ownerId("uuid")
                .build();
        underTest.save(expected);

        // when
        Wallet actual = underTest.findWalletByIdAndOwnerId(1L, "uuid").get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getByOwnerIdAndRefFrom() {
        // given
        Wallet expected = Wallet.builder()
                .id(1L)
                .balance(50000L)
                .refFrom("users")
                .ownerId("uuid")
                .build();
        underTest.save(expected);

        // when
        Wallet actual = underTest.findAndLockByOwnerIdAndRefFrom("uuid", "users").get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getWalletById() {
        // given
        Wallet expected = Wallet.builder()
                .id(1L)
                .balance(50000L)
                .refFrom("users")
                .ownerId("uuid")
                .build();
        underTest.save(expected);

        // when
        Wallet actual = underTest.findAndLockWalletById(1L).get();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void existsByOwnerIdAndRefFrom() {
        // given
        Wallet expected = Wallet.builder()
                .id(1L)
                .balance(50000L)
                .refFrom("users")
                .ownerId("uuid")
                .build();
        underTest.save(expected);

        // when
        Boolean actual = underTest.existsByOwnerIdAndRefFrom("uuid", "users");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void countAllByOwnerIdAndRefFrom() {
        // given
        List<Wallet> expected = Arrays.asList(
                Wallet.builder()
                        .id(1L)
                        .balance(50000L)
                        .refFrom("systems")
                        .ownerId("uuid")
                        .build(),
                Wallet.builder()
                        .id(2L)
                        .balance(50000L)
                        .refFrom("systems")
                        .ownerId("uuid")
                        .build()
        );
        underTest.saveAll(expected);

        // when
        long actual = underTest.countAllByOwnerIdAndRefFrom("uuid", "systems");

        // then
        assertThat(actual).isEqualTo(2);
    }
}