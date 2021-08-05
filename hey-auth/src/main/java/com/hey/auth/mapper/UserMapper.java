package com.hey.auth.mapper;

import com.hey.auth.dto.user.RegisterRequest;
import com.hey.auth.dto.vertx.RegisterRequestToChat;
import com.hey.auth.dto.user.UserDTO;
import com.hey.auth.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserDTO user2UserDTO(User user);

    User registerRequest2User(RegisterRequest registerRequest);

    @Mapping(target = "userName", source = "username")
    @Mapping(target="userId", source = "id")
    RegisterRequestToChat registerRequest2Chat(User user);
}
