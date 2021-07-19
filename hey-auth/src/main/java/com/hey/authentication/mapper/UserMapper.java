package com.hey.authentication.mapper;

import com.hey.authentication.dto.RegisterRequest;
import com.hey.authentication.dto.UserDTO;
import com.hey.authentication.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserDTO user2UserDTO(User user);

    User userDTO2User(UserDTO userDTO);

    User registerRequest2User(RegisterRequest registerRequest);
}
