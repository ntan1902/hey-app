package com.hey.lucky.mapper;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.user.UserReceiveInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface UserMapper {
    UserReceiveInfo userInfo2UserReceiveInfo(UserInfo userInfo);
}
