package com.hey.auth.service;

import com.hey.auth.dto.system.*;
import com.hey.auth.entity.System;
import com.hey.auth.entity.User;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.system.InvalidSoftTokenException;
import com.hey.auth.exception.system.SystemIdNotFoundException;
import com.hey.auth.exception.system.SystemKeyInvalidException;
import com.hey.auth.exception.user.PinNotMatchedException;
import com.hey.auth.exception.user.UserIdNotFoundException;
import com.hey.auth.jwt.JwtSoftTokenUtil;
import com.hey.auth.jwt.JwtSystemUtil;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.mapper.SystemMapper;
import com.hey.auth.repository.SoftTokenRepository;
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
    private final SoftTokenRepository softTokenRepository;
    private final RedisLockService redisLockService;


    @Override
    public System loadSystemBySystemName(String systemName) throws UsernameNotFoundException {
        log.info("Inside loadSystemBySystemName: {}", systemName);
        return systemRepository.findBySystemName(systemName)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("System " + systemName + " not found");
                });
    }

    @Override
    public System loadSystemById(String systemId) throws SystemIdNotFoundException {
        log.info("Inside loadSystemById: {}", systemId);
        return systemRepository.findById(systemId)
                .orElseThrow(() -> new SystemIdNotFoundException("System Id " + systemId + " not found"));
    }

    @Override
    public SystemLoginResponse login(SystemLoginRequest loginRequest) throws SystemKeyInvalidException {
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
    public AuthorizeResponse authorizeUser(AuthorizeRequest authorizeRequest) throws InvalidJwtTokenException, UserIdNotFoundException {
        log.info("Inside authorizeUser of SystemServiceImpl: {}", authorizeRequest);
        if (jwtUserUtil.validateToken(authorizeRequest.getJwtUser())) {
            String userId = jwtUserUtil.getUserIdFromJwt(authorizeRequest.getJwtUser());
//            if (!userRepository.existsById(userId)) {
//                throw new UserIdNotFoundException("User Id " + userId + " not found");
//            }
            return new AuthorizeResponse(userId);
        } else {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public SystemAuthorizeResponse authorizeSystem(SystemAuthorizeRequest authorizeRequest) throws InvalidJwtTokenException, SystemIdNotFoundException {
        log.info("Inside authorizeSystem of SystemServiceImpl: {}", authorizeRequest);
        if (jwtSystemUtil.validateToken(authorizeRequest.getJwtSystem())) {
            String systemId = jwtSystemUtil.getSystemIdFromJwt(authorizeRequest.getJwtSystem());
//            System system = systemRepository.findById(systemId)
//                    .orElseThrow(() -> new SystemIdNotFoundException("System Id " + systemId + " not found"));

            return new SystemAuthorizeResponse(systemId);
        } else {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
    }

    @Override
    public UserIdAmountResponse authorizeSoftToken(SoftTokenRequest softTokenRequest) throws PinNotMatchedException, UserIdNotFoundException, InvalidSoftTokenException {
        log.info("Inside authorizeSoftToken of SystemServiceImpl: {}", softTokenRequest);
        String softToken = softTokenRequest.getSoftToken();
        String lockKey = "soft_token"+softToken;

        redisLockService.lock(lockKey);
        if (!softTokenRepository.existsById(softToken)){
            redisLockService.unlock(lockKey);
            throw new InvalidSoftTokenException("Expired JWT soft token");
        } else {
            softTokenRepository.deleteById(softToken);
        }
        redisLockService.unlock(lockKey);

        if (jwtSoftTokenUtil.validateToken(softToken)) {
            // Parse information from jwt
            String userId = jwtSoftTokenUtil.getUserIdFromJwt(softToken);
            String pinFromJwt = jwtSoftTokenUtil.getPinFromJwt(softToken);
            long amountFromJwt = jwtSoftTokenUtil.getAmountFromJwt(softToken);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));

            // Check pin
            if (passwordEncoder.matches(pinFromJwt, user.getPin())) {
                return new UserIdAmountResponse(userId, amountFromJwt);
            } else {
                throw new PinNotMatchedException("Pin: " + pinFromJwt + " not matched");
            }

        } else {
            throw new InvalidSoftTokenException("Invalid JWT soft token");
        }
    }

    @Override
    public SystemDTO findById(String systemId) throws SystemIdNotFoundException {
        log.info("Inside findById of SystemServiceImpl: {}", systemId);
        System system = systemRepository.findById(systemId)
                .orElseThrow(() -> new SystemIdNotFoundException("System Id " + systemId + " not found"));
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
