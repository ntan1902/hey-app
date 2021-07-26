package com.hey.authentication.service;

import com.hey.authentication.dto.user.*;
import com.hey.authentication.dto.vertx.RegisterRequestToChat;
import com.hey.authentication.entity.User;
import com.hey.authentication.exception.user.EmptyPinException;
import com.hey.authentication.exception.user.PinNotMatchedException;
import com.hey.authentication.exception.user.UserIdNotFoundException;
import com.hey.authentication.jwt.JwtSoftTokenUtil;
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
    private final JwtSoftTokenUtil jwtSoftTokenUtil;
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

        // Get User after Authenticate
        User user = (User) authentication.getPrincipal();

        // Set JWT
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

        // Call api register to Vert.x
        registerToVertx(userRepository.save(user));
    }

    @Override
    public UserDTO findById() {
        log.info("Inside findById of UserServiceImpl");
        User user = getCurrentUser();
        return userMapper.user2UserDTO(user);
    }

    @Override
    public UserDTO findById(Long userId) {
        log.info("Inside findById({}) of UserServiceImpl", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User Id {} not found", userId);
                    throw new UserIdNotFoundException("User Id " + userId + " not found");
                });
        return userMapper.user2UserDTO(user);
    }

    @Override
    public void createPin(PinAmountRequest pinAmountRequest) {
        log.info("Inside createPin of UserServiceImpl: {}", pinAmountRequest);
        String hashPin = passwordEncoder.encode(pinAmountRequest.getPin());
        User user = getCurrentUser();
        user.setPin(hashPin);
        userRepository.save(user);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public SoftTokenResponse createSoftToken(PinAmountRequest pinAmountRequest) {
        log.info("Inside createSoftToken of UserServiceImpl: {}", pinAmountRequest);
        User user = getCurrentUser();

        // Check if user has pin
        if(user.getPin().isEmpty()) {
            throw new EmptyPinException("Pin is not created yet!");
        }

        if (!passwordEncoder.matches(pinAmountRequest.getPin(), user.getPin())) {
            log.error("Pin: {} not matched", pinAmountRequest.getPin());
            throw new PinNotMatchedException("Pin: " + pinAmountRequest.getPin() + " not matched");
        }

        String softToken = jwtSoftTokenUtil.generateToken(user, pinAmountRequest.getPin(), pinAmountRequest.getAmount());
        return new SoftTokenResponse(softToken);
    }

    @Override
    public HasPinResponse hasPin() {
        log.info("Inside hasPin of UserServiceImpl");
        User user = getCurrentUser();
        return new HasPinResponse(user.getPin().isEmpty());
    }

    private void registerToVertx(User user) {
        log.info("Inside registerToVertx of UserServiceImpl: {}", user);
        RegisterRequestToChat registerRequestToChat = userMapper.registerRequest2Chat(user);
        webClientBuilder.build()
                .post()
                .uri(CHAT_SERVICE + "/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequestToChat)
                .retrieve()
                .bodyToMono(RegisterRequestToChat.class)
                .block();
    }
}
