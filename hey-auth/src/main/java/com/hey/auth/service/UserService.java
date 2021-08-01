package com.hey.auth.service;

import com.hey.auth.dto.user.*;
import com.hey.auth.entity.User;
import com.hey.auth.exception.user.UsernameEmailExistedException;

public interface UserService {
    User loadUserById(String userId);

    void register(RegisterRequest registerRequest) throws UsernameEmailExistedException;

    UserDTO findById();

    UserDTO findById(String userId);

    void createPin(PinRequest pinRequest);

    SoftTokenResponse createSoftToken(PinAmountRequest pinAmountRequest);

    HasPinResponse hasPin();
}
