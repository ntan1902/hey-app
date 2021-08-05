package com.hey.auth.repository;


import com.hey.auth.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository underTest;

    @Test
    void findUserByUsername() {
        // given
        User expectedUser = User.builder()
                .id("uuid")
                .email("annt12@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("annt12")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedUser);

        // when
        User actualUser = underTest.findByUsername("annt12").get();

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void notExistByUsernameOrEmail() {
        // given
        User expectedUser = User.builder()
                .id("uuid")
                .email("annt12@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("annt12")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedUser);

        // when
        Boolean actual = underTest.existsByUsernameOrEmail("ntan", "ntan@gmail.com");

        // then
        assertThat(actual).isEqualTo(false);

    }

    @Test
    void existsByEmail() {
        // given
        User expectedUser = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("annt12")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedUser);

        // when
        Boolean actual = underTest.existsByUsernameOrEmail("ntan", "ntan@gmail.com");

        // then
        assertThat(actual).isEqualTo(true);

    }
    @Test
    void existsByUsername() {
        // given
        User expectedUser = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("ntan")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedUser);

        // when
        Boolean actual = underTest.existsByUsernameOrEmail("ntan", "annt12@gmail.com");

        // then
        assertThat(actual).isEqualTo(true);

    }

    @Test
    void existsByUsernameAndEmail() {
        // given
        User expectedUser = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("ntan")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        underTest.save(expectedUser);

        // when
        Boolean actual = underTest.existsByUsernameOrEmail("ntan", "ntan@gmail.com");

        // then
        assertThat(actual).isEqualTo(true);

    }
}