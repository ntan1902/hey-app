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
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl underTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtSoftTokenUtil jwtSoftTokenUtil;

    @Mock
    private JwtUserUtil jwtUserUtil;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ChatApi chatApi;

    @Mock
    private SoftTokenRepository softTokenRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void loadUserByUsername() {
        // given
        User expected = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();
        given(userRepository.findByUsername(expected.getUsername())).willReturn(Optional.of(expected));

        // when
        UserDetails actual = underTest.loadUserByUsername(expected.getUsername());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loadUserByUsernameWillThrowException() {
        // given

        given(userRepository.findByUsername("ntan")).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername("ntan"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User ntan not found");
    }

    @Test
    void loadUserById() throws UserIdNotFoundException {
        // given
        User expected = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();
        given(userRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        UserDetails actual = underTest.loadUserById(expected.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loadUserByIdWillThrowException() {
        // given
        given(userRepository.findById("ntan")).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.loadUserById("ntan"))
                .isInstanceOf(UserIdNotFoundException.class)
                .hasMessageContaining("User ntan not found");
    }

    @Test
    void register() throws UsernameEmailExistedException {
        // given
        RegisterRequest request = new RegisterRequest("ntan", "123", "ntan@gmail.com", "Trinh An");

        User user = User.builder()
                .id("uuid")
                .username("ntan")
                .password("123")
                .fullName("Trinh An")
                .build();

        given(
                userRepository.existsByUsernameOrEmail(
                        request.getUsername(),
                        request.getEmail()
                )
        ).willReturn(false);

        given(userMapper.registerRequest2User(request)).willReturn(user);

        given(passwordEncoder.encode(request.getPassword())).willReturn("abc");

        given(userRepository.save(user)).willReturn(user);

        doNothing().when(chatApi).register(user);

        // when
        underTest.register(request);

        // then
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void registerWillThrowException() throws UsernameEmailExistedException {
        // given
        RegisterRequest request = new RegisterRequest("ntan", "123", "ntan@gmail.com", "Trinh An");

        given(
                userRepository.existsByUsernameOrEmail(
                        request.getUsername(),
                        request.getEmail()
                )
        ).willReturn(true);

        // when

        // then
        assertThatThrownBy(() -> underTest.register(request))
                .isInstanceOf(UsernameEmailExistedException.class)
                .hasMessageContaining("username or email already existed");
    }

    @Test
    void findById() throws UserIdNotFoundException {
        // given
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserDTO expected = UserDTO.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .fullName("Trinh An")
                .build();
        given(userMapper.user2UserDTO(user)).willReturn(expected);

        // when
        UserDTO actual = underTest.findById();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindByIdWithParameterId() throws UserIdNotFoundException {
        // given
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserDTO expected = UserDTO.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .fullName("Trinh An")
                .build();
        given(userMapper.user2UserDTO(user)).willReturn(expected);

        // when
        UserDTO actual = underTest.findById(user.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindByIdWithParameterIdWillThrowException() {
        // given
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();
        given(userRepository.findById(user.getId())).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.findById(user.getId()))
                .isInstanceOf(UserIdNotFoundException.class)
                .hasMessageContaining("User Id " + user.getId() + " not found");

    }

    @SneakyThrows
    @Test
    void createPin() {
        // given
        PinRequest request = new PinRequest("123456");
        String hashPin = "sfsafsfsf";

        given(passwordEncoder.encode(request.getPin())).willReturn(hashPin);


        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        underTest.createPin(request);

        // then
        verify(userRepository).save(user);
    }

    @SneakyThrows
    @Test
    void createPinWillThrowAlreadyHavePin() {
        // given
        PinRequest request = new PinRequest("123456");
     
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        assertThatThrownBy(() -> underTest.createPin(request))
                .isInstanceOf(AlreadyHavePinException.class)
                .hasMessageContaining("User " + user.getId() + " already have pin");

        // then
        verify(userRepository, never()).save(user);
    }

    @Test
    void createSoftToken() throws PinNotMatchedException, EmptyPinException, UserIdNotFoundException {
        // given
        PinAmountRequest request = new PinAmountRequest("123456", 50L);

        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        given(passwordEncoder.matches(request.getPin(), user.getPin())).willReturn(true);

        String softToken = "1312312dsfasf";
        given(jwtSoftTokenUtil.generateToken(user, request.getPin(), request.getAmount())).willReturn(softToken);

        SoftTokenResponse expected = new SoftTokenResponse(softToken);

        // when
        SoftTokenResponse actual = underTest.createSoftToken(request);

        // then
        verify(softTokenRepository, times(1)).save(new SoftToken(softToken));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createSoftTokenWillThrowEmptyPinException() throws PinNotMatchedException, EmptyPinException {
        // given
        PinAmountRequest request = new PinAmountRequest("123456", 50L);

        User user = User.builder()
                .id("uuid")
                .pin("")
                .email("ntan@gmail.com")
                .username("ntan")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when

        // then
        assertThatThrownBy(() -> underTest.createSoftToken(request))
                .isInstanceOf(EmptyPinException.class)
                .hasMessageContaining("Pin is not created yet!");
    }

    @Test
    void createSoftTokenWillThrowPinNotMatchedException() throws PinNotMatchedException, EmptyPinException {
        // given
        PinAmountRequest request = new PinAmountRequest("123456", 50L);

        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        given(passwordEncoder.matches(request.getPin(), user.getPin())).willReturn(false);

        // when

        // then
        assertThatThrownBy(() -> underTest.createSoftToken(request))
                .isInstanceOf(PinNotMatchedException.class)
                .hasMessageContaining("Pin: " + request.getPin() + " not matched");
    }

    @Test
    void hasPinWillReturnTrue() throws UserIdNotFoundException {
        // given
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("123456")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        HasPinResponse expected = new HasPinResponse(true);

        // when
        HasPinResponse actual = underTest.hasPin();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void hasPinWillReturnFalse() throws UserIdNotFoundException {
        // given
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken("uuid",
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        HasPinResponse expected = new HasPinResponse(false);

        // when
        HasPinResponse actual = underTest.hasPin();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    void findUsernameById() {
        User user = User.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .pin("")
                .password("gdsgdsg")
                .fullName("Trinh An")
                .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UsernameResponse expected = new UsernameResponse(user.getUsername());

        // when
        UsernameResponse actual = underTest.findUsernameById(user.getId());

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, ntan@gmail.com, Trinh An, 2021-06-02T21:33:45.249967, 0111245677",
            "uuid2, ntan@gmail.com, Trinh An, 2021-06-02T21:33:45.249967, 0111245677",
            "uuid3, ntan@gmail.com, Trinh An, 2021-06-02T21:33:45.249967, 0111245677"})
    void editUser(String id, String email, String fullName, String dob, String phoneNumber) {
        // given
        User user = User.builder()
                .id(id)
                .fullName("An Nguyen")
                .email(email)
                .dob(LocalDateTime.parse(dob))
                .phoneNumber(phoneNumber)
                .build();

        EditUserRequest request = new EditUserRequest();
        request.setEmail(email);
        request.setFullName(fullName);
        request.setPhoneNumber(phoneNumber);
        request.setDob(dob);

        switch (id) {
            case "uuid1":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

                // when
                underTest.editUser(id, request);

                // then
                verify(userRepository, times(1)).save(user);
                break;
            case "uuid2":
                given(userRepository.findById(id)).willReturn(Optional.empty());
                assertThatThrownBy(() -> underTest.editUser(id, request))
                        .isInstanceOf(UserIdNotFoundException.class)
                        .hasMessageContaining("User Id " + id + " not found");
                verify(userRepository, never()).save(user);
                break;
            case "uuid3":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

                user.setEmail("dump@gmail.com");

                // when
                assertThatThrownBy(() -> underTest.editUser(id, request))
                        .isInstanceOf(UsernameEmailExistedException.class)
                        .hasMessageContaining("Email already exists. Please choose another");
                verify(userRepository, never()).save(user);
                break;
        }

    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, 123, 123456, 123456",
            "uuid2, 123, 123456, 123456",
            "uuid3, 123, 123456, 123456",
            "uuid4, 123, 12345, 123456"})
    void changePassword(String id, String oldPassword, String password, String confirmPassword) {
        // given
        User user = User.builder()
                .id(id)
                .password(oldPassword)
                .build();
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(id,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(oldPassword);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);

        switch (id) {
            case "uuid1":

                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
                given(passwordEncoder.encode(password)).willReturn("abc");

                // when
                underTest.changePassword(request);

                // then
                verify(userRepository, times(1)).save(user);
                break;
            case "uuid2":
                given(userRepository.findById(id)).willReturn(Optional.empty());
                assertThatThrownBy(() -> underTest.changePassword(request))
                        .isInstanceOf(UserIdNotFoundException.class)
                        .hasMessageContaining("User Id " + id + " not found");
                verify(userRepository, never()).save(user);
                break;
            case "uuid3":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(false);

                // when
                assertThatThrownBy(() -> underTest.changePassword(request))
                        .isInstanceOf(PasswordNotMatchedException.class)
                        .hasMessageContaining("Old password is not matched. Please try again");
                // then
                verify(userRepository, never()).save(user);
                break;
            case "uuid4":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
                // when
                assertThatThrownBy(() -> underTest.changePassword(request))
                        .isInstanceOf(PasswordNotMatchedException.class)
                        .hasMessageContaining("Confirm password is not matched. Please try again");

                // then
                verify(userRepository, never()).save(user);
                break;
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, 123456, 123457, 123457",
            "uuid2, 123456, 123457, 123457",
            "uuid3, 123456, 123457, 123457",
            "uuid4, 123456, 123457, 123457",
            "uuid5, 123456, 123457, 12345"})
    void changePin(String id, String oldPin, String pin, String confirmPin) {
        // given
        User user = User.builder()
                .id(id)
                .pin(oldPin)
                .build();
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(id,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ChangePinRequest request = new ChangePinRequest();
        request.setOldPin(oldPin);
        request.setPin(pin);
        request.setConfirmPin(confirmPin);

        switch (id) {
            case "uuid1":

                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(passwordEncoder.matches(oldPin, user.getPin())).willReturn(true);
                given(passwordEncoder.encode(pin)).willReturn("abc");

                // when
                underTest.changePin(request);

                // then
                verify(userRepository, times(1)).save(user);
                break;
            case "uuid2":
                given(userRepository.findById(id)).willReturn(Optional.empty());
                assertThatThrownBy(() -> underTest.changePin(request))
                        .isInstanceOf(UserIdNotFoundException.class)
                        .hasMessageContaining("User Id " + id + " not found");
                verify(userRepository, never()).save(user);
                break;
            case "uuid3":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(passwordEncoder.matches(oldPin, user.getPin())).willReturn(false);

                // when
                assertThatThrownBy(() -> underTest.changePin(request))
                        .isInstanceOf(PinNotMatchedException.class)
                        .hasMessageContaining("Old pin: " + oldPin + " not matched");

                // then
                verify(userRepository, never()).save(user);
                break;
            case "uuid4":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                user.setPin("");

                // when
                assertThatThrownBy(() -> underTest.changePin(request))
                        .isInstanceOf(EmptyPinException.class)
                        .hasMessageContaining("Pin is not created yet!");
                // then
                verify(userRepository, never()).save(user);
                break;
            case "uuid5":
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                given(passwordEncoder.matches(oldPin, user.getPin())).willReturn(true);
                // when
                assertThatThrownBy(() -> underTest.changePin(request))
                        .isInstanceOf(PinNotMatchedException.class)
                        .hasMessageContaining("Confirm pin is not matched. Please try again");

                // then
                verify(userRepository, never()).save(user);
                break;
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, abc",
            "uuid2, abc",
            "uuid3, abc"})
    void refreshToken(String id, String refreshToken) {
        // given
        User user = User.builder()
                .id(id)
                .build();
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        switch (id) {
            case "uuid1":
                given(refreshTokenRepository.existsById(refreshToken)).willReturn(true);
                given(jwtUserUtil.validateToken(refreshToken)).willReturn(true);
                given(jwtUserUtil.getUserIdFromJwt(refreshToken)).willReturn("uuid1");
                given(userRepository.findById("uuid1")).willReturn(Optional.of(user));
                given(jwtUserUtil.generateAccessToken(user)).willReturn("accessToken");

                // when
                RefreshTokenResponse response = underTest.refreshToken(request);

                // then
                assertThat(response.getAccessToken()).isEqualTo("accessToken");
                break;
            case "uuid2":
                given(refreshTokenRepository.existsById(refreshToken)).willReturn(false);

                assertThatThrownBy(() -> underTest.refreshToken(request))
                        .isInstanceOf(InvalidJwtTokenException.class)
                        .hasMessageContaining("Invalid Refresh Token");

                break;
            case "uuid3":
                given(refreshTokenRepository.existsById(refreshToken)).willReturn(true);
                given(jwtUserUtil.validateToken(refreshToken)).willReturn(false);

                assertThatThrownBy(() -> underTest.refreshToken(request))
                        .isInstanceOf(InvalidJwtTokenException.class)
                        .hasMessageContaining("Invalid Refresh Token");

                break;
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, an"})
    void searchUser(String id, String key){
        // given
        User user = User.builder()
                .id(id)
                .build();
        UserDTO userDTO = UserDTO.builder()
                .id(id)
                .build();

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(id,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        switch (id) {
            case "uuid1":
                given(userRepository.findAllByFullNameContainsOrEmailContainsAndIdIsNot(key, id)).willReturn(Collections.singletonList(user));
                given(userMapper.user2UserDTO(user)).willReturn(userDTO);

                // when
                List<UserDTO> userDTOS = underTest.searchUser(key);

                // then
                assertThat(userDTOS.size()).isEqualTo(1);
                assertThat(userDTOS.get(0)).isEqualTo(userDTO);
                break;
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, abc",
            "uuid2, abc",
            "uuid3, abc"})
    void logout(String id, String refreshToken) {
        // given
        User user = User.builder()
                .id(id)
                .build();
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(id,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LogOutRequest request = new LogOutRequest();
        request.setRefreshToken(refreshToken);

        switch (id) {
            case "uuid1":
                given(refreshTokenRepository.existsById(refreshToken)).willReturn(true);
                given(jwtUserUtil.validateToken(refreshToken)).willReturn(true);
                given(jwtUserUtil.getUserIdFromJwt(refreshToken)).willReturn(id);

                // when
                underTest.logout(request);

                // then
                verify(refreshTokenRepository, times(1)).deleteById(refreshToken);
                break;
            case "uuid2":
                given(refreshTokenRepository.existsById(refreshToken)).willReturn(false);

                assertThatThrownBy(() -> underTest.logout(request))
                        .isInstanceOf(InvalidJwtTokenException.class)
                        .hasMessageContaining("Invalid Refresh Token");

                break;
            case "uuid3":
                given(refreshTokenRepository.existsById(refreshToken)).willReturn(true);
                given(jwtUserUtil.validateToken(refreshToken)).willReturn(false);

                assertThatThrownBy(() -> underTest.logout(request))
                        .isInstanceOf(InvalidJwtTokenException.class)
                        .hasMessageContaining("Invalid Refresh Token");

                break;
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"uuid1, uri, mediumUri, minUri",
            "uuid2, uri, mediumUri, minUri",
            "uuid3, uri, mediumUri, minUri"})
    void updateAvatar(String id, String uri, String mediumUri, String minUri) {
        // given
        User user = User.builder()
                .id(id)
                .build();
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(id,
                null,
                user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UpdateAvatarRequest request = new UpdateAvatarRequest();
        request.setUri(uri);
        request.setMediumUri(mediumUri);
        request.setSmallUri(minUri);

        switch (id) {
            case "uuid1":
                given(userRepository.findById(id)).willReturn(Optional.of(user));

                // when
                underTest.updateAvatar(request);

                // then
                verify(userRepository, times(1)).save(user);
                break;
            case "uuid2":
                given(userRepository.findById(id)).willReturn(Optional.empty());
                assertThatThrownBy(() -> underTest.updateAvatar(request))
                        .isInstanceOf(UserIdNotFoundException.class)
                        .hasMessageContaining("User Id " + id + " not found");
                verify(userRepository, never()).save(user);
                break;
        }
    }
}