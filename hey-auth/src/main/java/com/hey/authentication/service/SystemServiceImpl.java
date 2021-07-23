package com.hey.authentication.service;

import com.hey.authentication.dto.system.*;
import com.hey.authentication.entity.System;
import com.hey.authentication.exception.jwt.InvalidJwtTokenException;
import com.hey.authentication.exception.system.SystemIdNotFoundException;
import com.hey.authentication.exception.system.SystemKeyInvalidException;
import com.hey.authentication.exception.user.UserIdNotFoundException;
import com.hey.authentication.jwt.JwtSoftTokenUtil;
import com.hey.authentication.jwt.JwtSystemUtil;
import com.hey.authentication.jwt.JwtUserUtil;
import com.hey.authentication.repository.SystemRepository;
import com.hey.authentication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class SystemServiceImpl implements SystemService {
    private final SystemRepository systemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUserUtil jwtUserUtil;
    private final JwtSystemUtil jwtSystemUtil;
    private final JwtSoftTokenUtil jwtSoftTokenUtil;


    @Override
    public System loadSystemBySystemName(String systemName) throws UsernameNotFoundException {
        log.info("Inside loadSystemBySystemName: {}", systemName);
        return systemRepository.findBySystemName(systemName)
                .orElseThrow(() -> {
                    log.error("System {} not found", systemName);
                    throw new UsernameNotFoundException("System " + systemName + " not found");
                });
    }

    @Override
    public System loadSystemById(Long systemId) {
        log.info("Inside loadSystemById: {}", systemId);
        return systemRepository.findById(systemId)
                .orElseThrow(() -> {
                    log.error("System Id {} not found", systemId);
                    throw new SystemIdNotFoundException("System Id " + systemId + " not found");
                });
    }

    @Override
    public SystemLoginResponse login(SystemLoginRequest loginRequest) {
        log.info("Inside login of SystemServiceImpl: {}", loginRequest);

        System system = loadSystemBySystemName(loginRequest.getSystemName());

        if (passwordEncoder.matches(loginRequest.getSystemKey(), system.getSystemKey())) {
            String jwt = jwtSystemUtil.generateToken(system);

            return new SystemLoginResponse(jwt, "Bearer");
        } else {
            throw new SystemKeyInvalidException("System not valid");
        }

    }

    @Override
    public AuthorizeResponse authorizeUser(AuthorizeRequest authorizeRequest) {
        log.info("Inside authorizeUser of SystemServiceImpl: {}", authorizeRequest);
        if (jwtUserUtil.validateToken(authorizeRequest.getJwtUser())) {
            Long userId = jwtUserUtil.getUserIdFromJwt(authorizeRequest.getJwtUser());
            if (!userRepository.existsById(userId)) {
                log.error("User Id {} not found", userId);
                throw new UserIdNotFoundException("User Id " + userId + " not found");
            }
            return new AuthorizeResponse(userId);
        } else {
            log.error("Invalid JWT token: {}", authorizeRequest.getJwtUser());
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public SystemAuthorizeResponse authorizeSystem(SystemAuthorizeRequest authorizeRequest) {
        log.info("Inside authorizeSystem of SystemServiceImpl: {}", authorizeRequest);
        if (jwtSystemUtil.validateToken(authorizeRequest.getJwtSystem())) {
            Long systemId = jwtSystemUtil.getSystemIdFromJwt(authorizeRequest.getJwtSystem());
            System system = systemRepository.findById(systemId)
                    .orElseThrow(() -> {
                        log.error("System Id {} not found", systemId);
                        throw new SystemIdNotFoundException("System Id " + systemId + " not found");
                    });

            return new SystemAuthorizeResponse(system.getSystemName());
        } else {
            log.error("Invalid JWT token: {}", authorizeRequest.getJwtSystem());
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public void authorizeSoftToken(SoftTokenRequest softTokenRequest) {
        log.info("Inside authorizeSoftToken of SystemServiceImpl: {}", softTokenRequest);

        String softToken = softTokenRequest.getSoftToken();
        if (jwtSoftTokenUtil.validateToken(softToken)) {
            Long userId = jwtSoftTokenUtil.getUserIdFromJwt(softToken);
            if (!userRepository.existsById(userId)) {
                log.error("User Id {} not found", userId);
                throw new UserIdNotFoundException("User Id " + userId + " not found");
            }
        } else {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public SystemDTO findById(Long systemId) {
        log.info("Inside findById of SystemServiceImpl: {}", systemId);
        System system = systemRepository.findById(systemId)
                .orElseThrow(() -> {
                    log.error("System Id {} not found", systemId);
                    throw new SystemIdNotFoundException("System Id " + systemId + " not found");
                });
        return new SystemDTO(system.getSystemName());
    }

}
