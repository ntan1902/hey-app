package com.hey.authentication.repository;


import com.hey.authentication.entity.System;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SystemRepositoryTest {
    @Autowired
    private SystemRepository underTest;

    @Test
    void findBySystemName() {
        // given
        System expectedSystem = System.builder()
                .systemName("payment")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedSystem);

        // when
        System actualSystem = underTest.findBySystemName("payment").get();

        // then
        assertThat(actualSystem).isEqualTo(actualSystem);
    }
}