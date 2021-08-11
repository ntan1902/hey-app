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
import com.hey.auth.mapper.UserMapper;
import com.hey.auth.properties.ServiceProperties;
import com.hey.auth.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

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
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ChatApi chatApi;

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

    @Test
    void createPin() throws UserIdNotFoundException {
        // given
        PinRequest request = new PinRequest("123456");
        String hashPin = "sfsafsfsf";

        given(passwordEncoder.encode(request.getPin())).willReturn(hashPin);


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
        underTest.createPin(request);

        // then
        verify(userRepository).save(user);
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
}