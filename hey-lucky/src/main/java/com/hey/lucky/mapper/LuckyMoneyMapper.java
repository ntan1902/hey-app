package com.hey.lucky.mapper;

import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.LuckyMoneyDetails;
import com.hey.lucky.entity.LuckyMoney;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface LuckyMoneyMapper {
    @Mappings({
            @Mapping(target = "luckyMoneyId", source = "id"),
            @Mapping(target = "totalBag", source = "numberBag"),
            @Mapping(target = "totalMoney", source = "amount"),
            @Mapping(target = "isExpired", expression = "java(luckyMoney.getExpiredAt().isBefore(LocalDateTime.now()))")
    })
    LuckyMoneyDTO luckyMoney2LuckyMoneyDTO(LuckyMoney luckyMoney);

    @Mappings({
            @Mapping(target = "totalBag", source = "numberBag"),
            @Mapping(target = "totalMoney", source = "amount"),
    })
    LuckyMoneyDetails luckyMoney2LuckyMoneyDetails(LuckyMoney luckyMoney);
}
