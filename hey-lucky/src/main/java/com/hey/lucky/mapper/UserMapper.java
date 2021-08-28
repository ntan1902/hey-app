package com.hey.lucky.mapper;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.user.UserReceivedInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserReceivedInfo userInfo2UserReceiveInfo(UserInfo userInfo);
}
