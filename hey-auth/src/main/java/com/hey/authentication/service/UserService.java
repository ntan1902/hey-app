package com.hey.authentication.service;

import com.hey.authentication.dto.user.LoginRequest;
import com.hey.authentication.dto.user.LoginResponse;
import com.hey.authentication.dto.user.RegisterRequest;
import com.hey.authentication.dto.user.UserDTO;
import com.hey.authentication.entity.User;

public interface UserService {
    User loadUserById(Long userId);

    LoginResponse login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    UserDTO findById();
}
