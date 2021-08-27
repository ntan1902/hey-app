package com.hey.auth.mapper;

import com.hey.auth.dto.user.RegisterRequest;
import com.hey.auth.dto.user.UserDTO;
import com.hey.auth.dto.vertx.RegisterRequestToChat;
import com.hey.auth.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
       userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void user2UserDTO() {
        // given
        User user = new User();
        user.setId("uuid");
        user.setPassword("uuid");
        user.setEmail("uuid@gmail.com");
        user.setFullName("uuid");
        user.setUsername("uuid");
        user.setAvatar("uuid.png");

        UserDTO userDTO = userMapper.user2UserDTO(user);

        assertThat(userDTO.getId()).isEqualTo(user.getId());
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDTO.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDTO.getAvatar()).isEqualTo(user.getAvatar());
    }

    @Test
    void registerRequest2User() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("email");
        request.setUsername("username");
        request.setPassword("password");
        request.setFullName("fullName");

        User user = userMapper.registerRequest2User(request);

        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getUsername()).isEqualTo(request.getUsername());
        assertThat(user.getPassword()).isEqualTo(request.getPassword());
        assertThat(user.getFullName()).isEqualTo(request.getFullName());
    }

    @Test
    void registerRequest2Chat() {
        User user = new User();
        user.setId("uuid");
        user.setPassword("uuid");
        user.setEmail("uuid@gmail.com");
        user.setFullName("uuid");
        user.setUsername("uuid");
        user.setAvatar("uuid.png");

        RegisterRequestToChat request = userMapper.registerRequest2Chat(user);

        assertThat(request.getUserId()).isEqualTo(user.getId());
        assertThat(request.getFullName()).isEqualTo(user.getFullName());
        assertThat(request.getUserName()).isEqualTo(user.getUsername());
    }
}