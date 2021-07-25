package com.hey.authentication.service;


import com.hey.authentication.dto.system.*;
import com.hey.authentication.entity.System;

import java.util.List;

public interface SystemService {
    System loadSystemBySystemName(String systemName);

    System loadSystemById(Long systemId);

    SystemLoginResponse login(SystemLoginRequest loginRequest);

    AuthorizeResponse authorizeUser(AuthorizeRequest authorizeRequest);

    SystemAuthorizeResponse authorizeSystem(SystemAuthorizeRequest authorizeRequest);

    UserIdAmountResponse authorizeSoftToken(SoftTokenRequest softTokenRequest);

    SystemDTO findById(Long systemId);

    List<SystemDTO> getSystems();
}
