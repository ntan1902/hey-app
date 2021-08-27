package com.hey.auth.mapper;

import com.hey.auth.dto.system.SystemDTO;
import com.hey.auth.entity.System;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class SystemMapperTest {
    private SystemMapper systemMapper;

    @BeforeEach
    void setUp() {
        systemMapper = Mappers.getMapper(SystemMapper.class);
    }

    @Test
    void system2systemDTO() {
        System system = new System();
        system.setId("uuid");
        system.setSystemKey("uuid");
        system.setSystemName("uuid");
        system.setNumberOfWallet(10);

        SystemDTO systemDTO = systemMapper.system2systemDTO(system);

        assertThat(systemDTO.getId()).isEqualTo(system.getId());
        assertThat(systemDTO.getSystemName()).isEqualTo(system.getSystemName());
        assertThat(systemDTO.getNumberOfWallet()).isEqualTo(system.getNumberOfWallet());
    }
}