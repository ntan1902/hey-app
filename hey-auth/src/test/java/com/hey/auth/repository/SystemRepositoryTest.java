package com.hey.auth.repository;


import com.hey.auth.entity.System;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SystemRepositoryTest {
    @Autowired
    private SystemRepository underTest;

    @Test
    void findBySystemName() {
        // given
        System expectedSystem = System.builder()
                .id("uuid")
                .systemName("systemmm")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedSystem);

        // when
        System actualSystem = underTest.findBySystemName("systemmm").get();

        // then
        Assertions.assertThat(actualSystem).isEqualTo(actualSystem);
    }

    @Test
    void findById() {
        // given
        System expectedSystem = System.builder()
                .id("uuid")
                .systemName("systemmm")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedSystem);

        // when
        System actualSystem = underTest.findById("uuid").get();

        // then
        Assertions.assertThat(actualSystem).isEqualTo(actualSystem);
    }
}