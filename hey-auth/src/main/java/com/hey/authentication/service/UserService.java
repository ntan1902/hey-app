package com.hey.authentication.service;

import com.hey.authentication.dto.user.*;
import com.hey.authentication.entity.User;

public interface UserService {
    User loadUserById(Long userId);

    LoginResponse login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    UserDTO findById();

    void createPin(PinRequest pinRequest);

    SoftTokenResponse createSoftToken(PinRequest pinRequest);
}
