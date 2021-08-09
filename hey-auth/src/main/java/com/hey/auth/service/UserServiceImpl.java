package com.hey.auth.service;

import com.hey.auth.api.ChatApi;
import com.hey.auth.dto.user.*;
import com.hey.auth.dto.vertx.RegisterRequestToChat;
import com.hey.auth.entity.User;
import com.hey.auth.exception.user.EmptyPinException;
import com.hey.auth.exception.user.PinNotMatchedException;
import com.hey.auth.exception.user.UserIdNotFoundException;
import com.hey.auth.exception.user.UsernameEmailExistedException;
import com.hey.auth.jwt.JwtSoftTokenUtil;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.mapper.UserMapper;
import com.hey.auth.properties.ServiceProperties;
import com.hey.auth.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Log4j2
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final JwtSoftTokenUtil jwtSoftTokenUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ChatApi chatApi;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("User " + username + " not found");
                });
    }

    @Override
    public User loadUserById(String userId) throws UserIdNotFoundException {
        log.info("Inside loadUserById: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User Id {} not found", userId);
                    return new UserIdNotFoundException("User " + userId + " not found");
                });
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) throws UsernameEmailExistedException {
        log.info("Inside register of UserServiceImpl: {}", registerRequest);

        // Check if username or email is existed
        if (Boolean.TRUE.equals(userRepository.existsByUsernameOrEmail(
                registerRequest.getUsername(),
                registerRequest.getEmail()
        ))) {
            throw new UsernameEmailExistedException("username or email already existed");
        }

        // Save new user
        User user = userMapper.registerRequest2User(registerRequest);

        user.setId(UUID.randomUUID().toString());
        user.setPassword(
                passwordEncoder.encode(registerRequest.getPassword())
        );

        // Call api register to Vert.x
        chatApi.register(userRepository.save(user));
    }

    @Override
    public UserDTO findById() {
        log.info("Inside findById of UserServiceImpl");
        User user = getCurrentUser();
        return userMapper.user2UserDTO(user);
    }

    @Override
    public UserDTO findById(String userId) throws UserIdNotFoundException {
        log.info("Inside findById({}) of UserServiceImpl", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
        return userMapper.user2UserDTO(user);
    }

    @Override
    public void createPin(PinRequest pinRequest) {
        log.info("Inside createPin of UserServiceImpl: {}", pinRequest);
        String hashPin = passwordEncoder.encode(pinRequest.getPin());
        User user = getCurrentUser();
        user.setPin(hashPin);
        userRepository.save(user);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public SoftTokenResponse createSoftToken(PinAmountRequest pinAmountRequest) throws EmptyPinException, PinNotMatchedException {
        log.info("Inside createSoftToken of UserServiceImpl: {}", pinAmountRequest);
        User user = getCurrentUser();

        // Check if user has pin
        if (user.getPin().isEmpty()) {
            throw new EmptyPinException("Pin is not created yet!");
        }

        if (!passwordEncoder.matches(pinAmountRequest.getPin(), user.getPin())) {
            throw new PinNotMatchedException("Pin: " + pinAmountRequest.getPin() + " not matched");
        }

        String softToken = jwtSoftTokenUtil.generateToken(user, pinAmountRequest.getPin(), pinAmountRequest.getAmount());
        return new SoftTokenResponse(softToken);
    }

    @Override
    public HasPinResponse hasPin() {
        log.info("Inside hasPin of UserServiceImpl");
        User user = getCurrentUser();
        return new HasPinResponse(!user.getPin().isEmpty());
    }

    @Override
    public UsernameResponse findUsernameById(String userId) throws UserIdNotFoundException {
        log.info("Inside findUsernameById of UserSerivceImpl: {}", userId);

        return userRepository.findById(userId)
                .map(user -> new UsernameResponse(user.getUsername()))
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
    }

}
