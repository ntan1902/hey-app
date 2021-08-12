package com.hey.auth.service;

import com.hey.auth.dto.user.*;
import com.hey.auth.entity.User;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.user.*;

public interface UserService {
    User loadUserById(String userId) throws UserIdNotFoundException;

    void register(RegisterRequest registerRequest) throws UsernameEmailExistedException;

    UserDTO findById() throws UserIdNotFoundException;

    UserDTO findById(String userId) throws UserIdNotFoundException;

    void createPin(PinRequest pinRequest) throws UserIdNotFoundException;

    SoftTokenResponse createSoftToken(PinAmountRequest pinAmountRequest) throws EmptyPinException, PinNotMatchedException, UserIdNotFoundException;

    HasPinResponse hasPin() throws UserIdNotFoundException;

    UsernameResponse findUsernameById(String userId) throws UserIdNotFoundException;

    void editUser(String userId, EditUserRequest request) throws UsernameEmailExistedException, UserIdNotFoundException;

    void changePassword(ChangePasswordRequest request) throws PasswordNotMatchedException, UserIdNotFoundException;

    void changePin(ChangePinRequest request) throws EmptyPinException, PinNotMatchedException, UserIdNotFoundException;

    RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws InvalidJwtTokenException, UserIdNotFoundException;
}
