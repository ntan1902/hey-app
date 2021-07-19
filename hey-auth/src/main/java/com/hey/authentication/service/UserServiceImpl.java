package com.hey.authentication.service;

import com.hey.authentication.dto.LoginRequest;
import com.hey.authentication.dto.LoginResponse;
import com.hey.authentication.dto.RegisterRequest;
import com.hey.authentication.dto.UserDTO;
import com.hey.authentication.entity.User;
import com.hey.authentication.exception.UserIdNotFoundException;
import com.hey.authentication.jwt.JwtUtil;
import com.hey.authentication.mapper.UserMapper;
import com.hey.authentication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User {} not found", username);
                    throw new UsernameNotFoundException("User " + username + " not found");
                });
    }

    @Override
    public User loadUserById(Long userId) {
        log.info("Inside loadUserById: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User Id {} not found", userId);
                    throw new UserIdNotFoundException("User " + userId + " not found");
                });
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Inside login of UserServiceImpl: {}", loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user =  (User) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(user);
//        RefreshToken refreshToken = refreshTokenService.insert(user.getId());

        UserDTO userDTO = this.userMapper.user2UserDTO(user);

        return new LoginResponse(userDTO, jwt, "Bearer");
    }

    @Override
    public UserDTO register(RegisterRequest registerRequest) {
        log.info("Inside register of UserServiceImpl: {}", registerRequest);

        User user = userMapper.registerRequest2User(registerRequest);
        user.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );

        userRepository.save(user);
        return this.userMapper.user2UserDTO(user);
    }
}
