package com.hey.authentication.service;

import com.hey.authentication.dto.user.LoginRequest;
import com.hey.authentication.dto.user.LoginResponse;
import com.hey.authentication.dto.user.RegisterRequest;
import com.hey.authentication.dto.user.UserDTO;
import com.hey.authentication.dto.vertx.RegisterRequestToChat;
import com.hey.authentication.entity.User;
import com.hey.authentication.exception.user.UserIdNotFoundException;
import com.hey.authentication.jwt.JwtUserUtil;
import com.hey.authentication.mapper.UserMapper;
import com.hey.authentication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUserUtil jwtUserUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final WebClient.Builder webClientBuilder;

    private static final String CHAT_SERVICE = "http://localhost:8080";

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
        log.info("Authentication: {}", authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user =  (User) authentication.getPrincipal();

        String jwt = jwtUserUtil.generateToken(user);

        return new LoginResponse(jwt, "Bearer");
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        log.info("Inside register of UserServiceImpl: {}", registerRequest);

        User user = userMapper.registerRequest2User(registerRequest);
        user.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );

        userRepository.save(user);
//        registerToVertx(userRepository.save(user));
    }

    @Override
    public UserDTO findById() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.user2UserDTO(user);
    }

    private void registerToVertx(User user) {
        log.info("Call register api to vertx: {}", user);
        RegisterRequestToChat registerRequestToChat = userMapper.registerRequest2Chat(user);
        webClientBuilder.build()
                .post()
                .uri(CHAT_SERVICE + "/api/public/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequestToChat)
                .retrieve()
                .bodyToMono(RegisterRequestToChat.class)
                .block();
    }
}
