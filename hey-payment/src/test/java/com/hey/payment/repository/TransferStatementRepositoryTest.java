package com.hey.payment.repository;

import com.hey.payment.entity.TransferStatement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TransferStatementRepositoryTest {
    @Autowired
    private TransferStatementRepository underTest;

    @Test
    void findAllBySourceIdOrTargetId() {
        // given
        List<TransferStatement> expected =
                Arrays.asList(
                        TransferStatement.builder()
                                .id(1L)
                                .amount(50000L)
                                .sourceId(1L)
                                .targetId(2L)
                                .transferFee(0L)
                                .transferType("transfer")
                                .status(2)
                                .build(),
                        TransferStatement.builder()
                                .id(2L)
                                .amount(50000L)
                                .sourceId(2L)
                                .targetId(1L)
                                .transferFee(0L)
                                .transferType("transfer")
                                .status(2)
                                .build()
                );
        underTest.saveAll(expected);

        // when
        List<TransferStatement> actual = underTest.findAllBySourceIdOrTargetId(1L);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}