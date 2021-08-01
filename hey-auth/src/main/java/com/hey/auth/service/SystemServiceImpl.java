package com.hey.auth.service;

import com.hey.auth.dto.system.*;
import com.hey.auth.entity.System;
import com.hey.auth.entity.User;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.system.SystemIdNotFoundException;
import com.hey.auth.exception.system.SystemKeyInvalidException;
import com.hey.auth.exception.user.PinNotMatchedException;
import com.hey.auth.exception.user.UserIdNotFoundException;
import com.hey.auth.jwt.JwtSoftTokenUtil;
import com.hey.auth.jwt.JwtSystemUtil;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.mapper.SystemMapper;
import com.hey.auth.repository.SystemRepository;
import com.hey.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    private final SystemMapper systemMapper;


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
    public System loadSystemById(String systemId) {
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
            String userId = jwtUserUtil.getUserIdFromJwt(authorizeRequest.getJwtUser());
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
            String systemId = jwtSystemUtil.getSystemIdFromJwt(authorizeRequest.getJwtSystem());
            System system = systemRepository.findById(systemId)
                    .orElseThrow(() -> {
                        log.error("System Id {} not found", systemId);
                        throw new SystemIdNotFoundException("System Id " + systemId + " not found");
                    });

            return new SystemAuthorizeResponse(systemId,system.getSystemName());
        } else {
            log.error("Invalid JWT token: {}", authorizeRequest.getJwtSystem());
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public UserIdAmountResponse authorizeSoftToken(SoftTokenRequest softTokenRequest) {
        log.info("Inside authorizeSoftToken of SystemServiceImpl: {}", softTokenRequest);

        String softToken = softTokenRequest.getSoftToken();
        if (jwtSoftTokenUtil.validateToken(softToken)) {
            // Parse information from jwt
            String userId = jwtSoftTokenUtil.getUserIdFromJwt(softToken);
            String pinFromJwt = jwtSoftTokenUtil.getPinFromJwt(softToken);
            long amountFromJwt = jwtSoftTokenUtil.getAmountFromJwt(softToken);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User Id {} not found", userId);
                        throw new UserIdNotFoundException("User Id " + userId + " not found");
                    });

            // Check pin
            if (passwordEncoder.matches(pinFromJwt, user.getPin())) {
                return new UserIdAmountResponse(userId, amountFromJwt);
            } else {
                log.error("Pin: {} not matched", pinFromJwt);
                throw new PinNotMatchedException("Pin: " + pinFromJwt + " not matched");
            }

        } else {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public SystemDTO findById(String systemId) {
        log.info("Inside findById of SystemServiceImpl: {}", systemId);
        System system = systemRepository.findById(systemId)
                .orElseThrow(() -> {
                    log.error("System Id {} not found", systemId);
                    throw new SystemIdNotFoundException("System Id " + systemId + " not found");
                });
        return systemMapper.system2systemDTO(system);
    }

    @Override
    public List<SystemDTO> getSystems() {
        log.info("Inside getSystems of SystemServiceImpl");
        return systemRepository.findAll()
                .stream()
                .map(systemMapper::system2systemDTO)
                .collect(Collectors.toList());
    }

}