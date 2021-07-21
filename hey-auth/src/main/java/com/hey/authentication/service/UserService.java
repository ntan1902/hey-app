package com.hey.authentication.service;

import com.hey.authentication.dto.api.LoginRequest;
import com.hey.authentication.dto.api.LoginResponse;
import com.hey.authentication.dto.api.RegisterRequest;
import com.hey.authentication.dto.api.UserDTO;
import com.hey.authentication.entity.User;

public interface UserService {
    User loadUserById(Long userId);

    LoginResponse login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    UserDTO findById();
}
