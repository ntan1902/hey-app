package com.hey.payment.mapper;

import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.Wallet;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",injectionStrategy = InjectionStrategy.FIELD)
public interface WalletMapper {
    WalletDTO wallet2WalletDTO(Wallet wallet);

    @Mapping(source = "id",target = "walletId")
    WalletSystemDTO wallet2WalletSystemDTO(Wallet wallet);
}
