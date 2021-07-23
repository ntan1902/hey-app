package com.hey.payment.mapper;

import com.hey.payment.dto.user.TransferStatementDTO;
import com.hey.payment.entity.TransferStatement;
import com.hey.payment.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface TransferStatementMapper {
    TransferStatementDTO ts2TsDTO(TransferStatement transferStatement);

}
