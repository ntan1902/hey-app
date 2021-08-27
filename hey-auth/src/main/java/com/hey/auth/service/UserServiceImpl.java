package com.hey.auth.service;

import com.hey.auth.api.ChatApi;
import com.hey.auth.dto.user.*;
import com.hey.auth.entity.SoftToken;
import com.hey.auth.entity.User;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.user.*;
import com.hey.auth.jwt.JwtSoftTokenUtil;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.mapper.UserMapper;
import com.hey.auth.repository.RefreshTokenRepository;
import com.hey.auth.repository.SoftTokenRepository;
import com.hey.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final JwtSoftTokenUtil jwtSoftTokenUtil;
    private final JwtUserUtil jwtUserUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ChatApi chatApi;
    private final SoftTokenRepository softTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

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
        user.setPin("");

        // Call api register to Vert.x
        chatApi.register(userRepository.save(user));
    }

    @Override
    public UserDTO findById() throws UserIdNotFoundException {
        log.info("Inside findById of UserServiceImpl");
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
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
    public void createPin(PinRequest pinRequest) throws UserIdNotFoundException, AlreadyHavePinException {
        log.info("Inside createPin of UserServiceImpl: {}", pinRequest);
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
        if (!user.getPin().isEmpty()) {
            throw new AlreadyHavePinException("User " + userId + " already have pin");
        }

        String hashPin = passwordEncoder.encode(pinRequest.getPin());
        user.setPin(hashPin);
        userRepository.save(user);
    }


    @Override
    public SoftTokenResponse createSoftToken(PinAmountRequest pinAmountRequest) throws EmptyPinException, PinNotMatchedException, UserIdNotFoundException {
        log.info("Inside createSoftToken of UserServiceImpl: {}", pinAmountRequest);
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));

        // Check if user has pin
        if (user.getPin().isEmpty()) {
            throw new EmptyPinException("Pin is not created yet!");
        }

        if (!passwordEncoder.matches(pinAmountRequest.getPin(), user.getPin())) {
            throw new PinNotMatchedException("Pin: " + pinAmountRequest.getPin() + " not matched");
        }
        String softToken = jwtSoftTokenUtil.generateToken(user, pinAmountRequest.getPin(), pinAmountRequest.getAmount());
        softTokenRepository.save(new SoftToken(softToken));
        return new SoftTokenResponse(softToken);
    }

    @Override
    public HasPinResponse hasPin() throws UserIdNotFoundException {
        log.info("Inside hasPin of UserServiceImpl");
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
        return new HasPinResponse(!user.getPin().isEmpty());
    }

    @Override
    public UsernameResponse findUsernameById(String userId) throws UserIdNotFoundException {
        log.info("Inside findUsernameById of UserServiceImpl: {}", userId);

        return userRepository.findById(userId)
                .map(user -> new UsernameResponse(user.getUsername()))
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
    }

    @Override
    public void editUser(String userId, EditUserRequest request) throws UsernameEmailExistedException, UserIdNotFoundException {
        log.info("Inside editUser of UserServiceImpl: {}", request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));

        String email = request.getEmail();
        String fullName = request.getFullName();
        String dob = request.getDob();
        String phoneNumber = request.getPhoneNumber();

        boolean emailPresent = userRepository
                .findByEmail(email)
                .isPresent();

        if (emailPresent && !user.getEmail().equals(email)) {
            throw new UsernameEmailExistedException("Email already exists. Please choose another");
        }

        user.setEmail(email);
        user.setFullName(fullName);
        user.setDob(LocalDateTime.parse(dob));
        user.setPhoneNumber(phoneNumber);

        userRepository.save(user);

    }

    @Override
    public void changePassword(ChangePasswordRequest request) throws PasswordNotMatchedException, UserIdNotFoundException {
        log.info("Inside changePassword of UserServiceImpl: {}", request);
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));

        String oldPassword = request.getOldPassword();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordNotMatchedException("Old password is not matched. Please try again");
        }

        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchedException("Confirm password is not matched. Please try again");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public void changePin(ChangePinRequest request) throws EmptyPinException, PinNotMatchedException, UserIdNotFoundException {
        log.info("Inside changePin of UserServiceImpl: {}", request);
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));

        // Check if user has pin
        if (user.getPin().isEmpty()) {
            throw new EmptyPinException("Pin is not created yet!");
        }

        String oldPin = request.getOldPin();
        String pin = request.getPin();
        String confirmPin = request.getConfirmPin();

        if (!passwordEncoder.matches(oldPin, user.getPin())) {
            throw new PinNotMatchedException("Old pin: " + oldPin + " not matched");
        }

        if (!pin.equals(confirmPin)) {
            throw new PinNotMatchedException("Confirm pin is not matched. Please try again");
        }
        user.setPin(passwordEncoder.encode(pin));
        userRepository.save(user);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws InvalidJwtTokenException, UserIdNotFoundException {
        log.info("Inside refreshToken of UserServiceImpl: {}", request);

        String refreshToken = request.getRefreshToken();
        if (refreshTokenRepository.existsById(refreshToken)
                && jwtUserUtil.validateToken(refreshToken)) {
            String userId = jwtUserUtil.getUserIdFromJwt(refreshToken);
            String accessToken = jwtUserUtil.generateAccessToken(
                    this.loadUserById(userId)
            );

            return new RefreshTokenResponse(accessToken, "Bearer");
        } else {
            throw new InvalidJwtTokenException("Invalid Refresh Token");
        }
    }

    @Override
    public List<UserDTO> searchUser(String key) {
        log.info("Inside searchUser of UserServiceImpl: {}", key);
        String userId = getCurrentUserId();
        return userRepository.findAllByFullNameContainsOrEmailContainsAndIdIsNot(key, userId)
                .stream()
                .map(userMapper::user2UserDTO)
                .collect(Collectors.toList());

    }

    @Override
    public void logout(LogOutRequest request) throws InvalidJwtTokenException {
        log.info("Inside logout of UserServiceImpl: {}", request);
        String refreshToken = request.getRefreshToken();
        if (refreshTokenRepository.existsById(refreshToken)
                && jwtUserUtil.validateToken(refreshToken)) {
            String userId = jwtUserUtil.getUserIdFromJwt(refreshToken);
            String currentUserId = getCurrentUserId();

            if (userId.equals(currentUserId)) {
                refreshTokenRepository.deleteById(refreshToken);
            }

        } else {
            throw new InvalidJwtTokenException("Invalid Refresh Token");
        }
    }

    @Override
    public void updateAvatar(UpdateAvatarRequest request) throws UserIdNotFoundException {
        String userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User Id " + userId + " not found"));
        log.info("User " + userId + " update avatar");
        user.setAvatar(request.getUri());
        user.setMediumAvatar(request.getMediumUri());
        user.setSmallAvatar(request.getSmallUri());
        userRepository.save(user);
    }

}
