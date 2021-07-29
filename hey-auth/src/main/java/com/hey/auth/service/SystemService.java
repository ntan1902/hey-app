package com.hey.auth.service;


import com.hey.auth.dto.system.*;
import com.hey.auth.entity.System;

import java.util.List;

public interface SystemService {
    System loadSystemBySystemName(String systemName);

    System loadSystemById(String systemId);

    SystemLoginResponse login(SystemLoginRequest loginRequest);

    AuthorizeResponse authorizeUser(AuthorizeRequest authorizeRequest);

    SystemAuthorizeResponse authorizeSystem(SystemAuthorizeRequest authorizeRequest);

    UserIdAmountResponse authorizeSoftToken(SoftTokenRequest softTokenRequest);

    SystemDTO findById(String systemId);

    List<SystemDTO> getSystems();
}
