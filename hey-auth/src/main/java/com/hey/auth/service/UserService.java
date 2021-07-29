package com.hey.auth.service;

import com.hey.auth.dto.user.*;
import com.hey.auth.entity.User;

public interface UserService {
    User loadUserById(String userId);

    void register(RegisterRequest registerRequest);

    UserDTO findById();

    UserDTO findById(String userId);

    void createPin(PinAmountRequest pinAmountRequest);

    SoftTokenResponse createSoftToken(PinAmountRequest pinAmountRequest);

    HasPinResponse hasPin();
}
