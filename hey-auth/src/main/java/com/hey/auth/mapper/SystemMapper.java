package com.hey.auth.mapper;

import com.hey.auth.dto.system.SystemDTO;
import com.hey.auth.entity.System;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SystemMapper {
    SystemDTO system2systemDTO(System system);
}
