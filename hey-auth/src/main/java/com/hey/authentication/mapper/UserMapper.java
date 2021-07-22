package com.hey.authentication.mapper;

import com.hey.authentication.dto.api.RegisterRequest;
import com.hey.authentication.dto.vertx.RegisterRequestToChat;
import com.hey.authentication.dto.api.UserDTO;
import com.hey.authentication.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserDTO user2UserDTO(User user);

    User userDTO2User(UserDTO userDTO);

    User registerRequest2User(RegisterRequest registerRequest);

    @Mapping(target = "userName", source = "username")
    @Mapping(target="userId", source = "id")
    RegisterRequestToChat registerRequest2Chat(User user);
}
