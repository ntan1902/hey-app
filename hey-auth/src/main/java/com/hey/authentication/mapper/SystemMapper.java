package com.hey.authentication.mapper;

import com.hey.authentication.dto.system.SystemDTO;
import com.hey.authentication.entity.System;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SystemMapper {
    SystemDTO system2systemDTO(System system);
}
