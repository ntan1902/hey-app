package com.hey.authentication.service;

import com.hey.authentication.dto.LoginRequest;
import com.hey.authentication.dto.LoginResponse;
import com.hey.authentication.dto.RegisterRequest;
import com.hey.authentication.dto.UserDTO;
import com.hey.authentication.entity.User;

public interface UserService {
    User loadUserById(Long userId);

    LoginResponse login(LoginRequest loginRequest);

    UserDTO register(RegisterRequest registerRequest);
}
