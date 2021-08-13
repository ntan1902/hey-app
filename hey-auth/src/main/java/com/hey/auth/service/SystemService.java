package com.hey.auth.service;


import com.hey.auth.dto.system.*;
import com.hey.auth.entity.System;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.system.InvalidSoftTokenException;
import com.hey.auth.exception.system.SystemIdNotFoundException;
import com.hey.auth.exception.system.SystemKeyInvalidException;
import com.hey.auth.exception.user.PinNotMatchedException;
import com.hey.auth.exception.user.UserIdNotFoundException;

import java.util.List;

public interface SystemService {
    System loadSystemBySystemName(String systemName);

    System loadSystemById(String systemId) throws SystemIdNotFoundException;

    SystemLoginResponse login(SystemLoginRequest loginRequest) throws SystemKeyInvalidException;

    AuthorizeResponse authorizeUser(AuthorizeRequest authorizeRequest) throws InvalidJwtTokenException, UserIdNotFoundException;

    SystemAuthorizeResponse authorizeSystem(SystemAuthorizeRequest authorizeRequest) throws InvalidJwtTokenException, SystemIdNotFoundException;

    UserIdAmountResponse authorizeSoftToken(SoftTokenRequest softTokenRequest) throws PinNotMatchedException, InvalidJwtTokenException, UserIdNotFoundException, InvalidSoftTokenException;

    SystemDTO findById(String systemId) throws SystemIdNotFoundException;

    List<SystemDTO> getSystems();
}
