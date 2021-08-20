package com.hey.payment.mapper;

import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.entity.TransferStatement;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface TransferStatementMapper {
    @Mapping(target = "description", source = "message")
    TransferStatementDTO ts2TsDTO(TransferStatement transferStatement);

}
