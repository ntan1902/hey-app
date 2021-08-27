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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SystemServiceImplTest {
    @InjectMocks
    private SystemServiceImpl underTest;

    @Mock
    private SystemRepository systemRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtSystemUtil jwtSystemUtil;

    @Mock
    private JwtUserUtil jwtUserUtil;

    @Mock
    private JwtSoftTokenUtil jwtSoftTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SystemMapper systemMapper;

    @Mock
    private RedisLockService redisLockService;

    @Mock
    private SoftTokenRepository softTokenRepository;

    @Test
    void loadSystemBySystemName() {
        // given
        System expected = System.builder()
                .systemName("payment")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        given(systemRepository.findBySystemName("payment")).willReturn(Optional.of(expected));

        // when
        System actual = underTest.loadSystemBySystemName("payment");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loadSystemBySystemNameWillThrowException() {
        assertThatThrownBy(() -> underTest.loadSystemBySystemName("dump"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("System " + "dump" + " not found");

    }

    @Test
    void loadSystemById() throws SystemIdNotFoundException {
        // given
        System expected = System.builder()
                .id("uuid")
                .systemName("payment")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        given(systemRepository.findById("uuid")).willReturn(Optional.of(expected));

        // when
        System actual = underTest.loadSystemById("uuid");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void loadSystemByIdWillThrowException() {
        // given
        String systemId = "uuid";

        // then
        assertThatThrownBy(() -> underTest.loadSystemById(systemId))
                .isInstanceOf(SystemIdNotFoundException.class)
                .hasMessageContaining("System Id " + systemId + " not found");
    }

    @Test
    void login() throws SystemKeyInvalidException {
        // given
        SystemLoginRequest loginRequest = new SystemLoginRequest(
                "payment",
                "123456"
        );
        System system = System.builder()
                .id("uuid")
                .systemName("payment")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        String expected = jwtSystemUtil.generateToken(system);

        given(systemRepository.findBySystemName("payment")).willReturn(Optional.of(system));
        given(passwordEncoder.matches(loginRequest.getSystemKey(), system.getSystemKey())).willReturn(true);
        // when
        SystemLoginResponse actual = underTest.login(loginRequest);

        // then
        assertThat(actual.getAccessToken()).isEqualTo(expected);
    }

    @Test
    void loginWillThrow() {
        // given
        SystemLoginRequest loginRequest = new SystemLoginRequest(
                "payment",
                "123456"
        );
        System system = System.builder()
                .id("uuid")
                .systemName("payment")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();

        given(systemRepository.findBySystemName("payment")).willReturn(Optional.of(system));
        given(passwordEncoder.matches(loginRequest.getSystemKey(), system.getSystemKey())).willReturn(false);
        // when

        // then
        assertThatThrownBy(() -> underTest.login(loginRequest))
                .isInstanceOf(SystemKeyInvalidException.class)
                .hasMessageContaining("System not valid");
    }

    @Test
    void authorizeUser() throws InvalidJwtTokenException, UserIdNotFoundException {
        // given
        AuthorizeRequest request = new AuthorizeRequest(
                "dump"
        );
        String expected = "uuid";
        given(jwtUserUtil.validateToken(request.getJwtUser())).willReturn(true);
        given(jwtUserUtil.getUserIdFromJwt(request.getJwtUser())).willReturn(expected);

        // when
        AuthorizeResponse actual = underTest.authorizeUser(request);

        // then
        assertThat(actual.getUserId()).isEqualTo(expected);

    }

    @Test
    void authorizeUserWillThrowInvalidJwtToken() {
        // given
        AuthorizeRequest request = new AuthorizeRequest(
                "dump"
        );
        given(jwtUserUtil.validateToken(request.getJwtUser())).willReturn(false);

        // when

        // then
        assertThatThrownBy(() -> underTest.authorizeUser(request))
                .isInstanceOf(InvalidJwtTokenException.class)
                .hasMessageContaining("Invalid JWT token");

    }

    @Test
    void authorizeSystem() throws InvalidJwtTokenException, SystemIdNotFoundException {
        // given
        SystemAuthorizeRequest request = new SystemAuthorizeRequest(
                "dump"
        );
        String systemId = "uuid";
        System expected = System.builder()
                .id("uuid")
                .systemName("payment")
                .systemKey("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        given(jwtSystemUtil.validateToken(request.getJwtSystem())).willReturn(true);
        given(jwtSystemUtil.getSystemIdFromJwt(request.getJwtSystem())).willReturn(systemId);

        // when
        SystemAuthorizeResponse actual = underTest.authorizeSystem(request);

        // then
        assertThat(actual.getSystemId()).isEqualTo(expected.getId());

    }

    @Test
    void authorizeSystemWillThrowInvalidJwtToken() {
        // given
        SystemAuthorizeRequest request = new SystemAuthorizeRequest(
                "dump"
        );

        given(jwtSystemUtil.validateToken(request.getJwtSystem())).willReturn(false);

        // when

        // then
        assertThatThrownBy(() -> underTest.authorizeSystem(request))
                .isInstanceOf(InvalidJwtTokenException.class)
                .hasMessageContaining("Invalid JWT token");

    }

    @Test
    void authorizeSoftToken() throws PinNotMatchedException, UserIdNotFoundException, InvalidSoftTokenException {
        // given
        SoftTokenRequest request = new SoftTokenRequest(
                "dump"
        );
        String userId = "uuid";
        User user = User.builder()
                .email("ntan1902@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("ntan")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();
        UserIdAmountResponse expected = new UserIdAmountResponse(
                userId,
                50000L
        );

        given(softTokenRepository.existsById(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.validateToken(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.getUserIdFromJwt(request.getSoftToken())).willReturn(userId);
        given(jwtSoftTokenUtil.getPinFromJwt(request.getSoftToken())).willReturn("123456");
        given(jwtSoftTokenUtil.getAmountFromJwt(request.getSoftToken())).willReturn(50000L);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("123456", user.getPin())).willReturn(true);

        // when
        UserIdAmountResponse actual = underTest.authorizeSoftToken(request);

        // then
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void authorizeSoftTokenWillThrowUserIdNotFound() {
        // given
        SoftTokenRequest request = new SoftTokenRequest(
                "dump"
        );
        String expected = "uuid";

        given(softTokenRepository.existsById(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.validateToken(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.getUserIdFromJwt(request.getSoftToken())).willReturn(expected);
        given(jwtSoftTokenUtil.getPinFromJwt(request.getSoftToken())).willReturn("123456");
        given(jwtSoftTokenUtil.getAmountFromJwt(request.getSoftToken())).willReturn(50000L);
        given(userRepository.findById(expected)).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.authorizeSoftToken(request))
                .isInstanceOf(UserIdNotFoundException.class)
                .hasMessageContaining("User Id " + expected + " not found");

    }

    @Test
    void authorizeSoftTokenWillThrowInvalidJwtToken() {
        // given
        SoftTokenRequest request = new SoftTokenRequest(
                "dump"
        );

        given(softTokenRepository.existsById(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.validateToken(request.getSoftToken())).willReturn(false);

        // when

        // then
        assertThatThrownBy(() -> underTest.authorizeSoftToken(request))
                .isInstanceOf(InvalidSoftTokenException.class)
                .hasMessageContaining("Invalid JWT soft token");

    }

    @Test
    void authorizeSoftTokenWillThrowPinNotMatched(){
        // given
        SoftTokenRequest request = new SoftTokenRequest(
                "dump"
        );
        String expected = "uuid";
        User user = User.builder()
                .email("ntan1902@gmail.com")
                .password("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .fullName("Trinh an")
                .username("ntan")
                .pin("$2a$10$atTTVVOQoQMksMstiYp3/u6tQaYRG/6S5IrMJmEkw8Yw70kKI9LW2")
                .build();

        given(softTokenRepository.existsById(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.validateToken(request.getSoftToken())).willReturn(true);
        given(jwtSoftTokenUtil.getUserIdFromJwt(request.getSoftToken())).willReturn(expected);
        given(jwtSoftTokenUtil.getPinFromJwt(request.getSoftToken())).willReturn("123456");
        given(jwtSoftTokenUtil.getAmountFromJwt(request.getSoftToken())).willReturn(50000L);
        given(userRepository.findById(expected)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("123456", user.getPin())).willReturn(false);

        // when

        // then
        assertThatThrownBy(() -> underTest.authorizeSoftToken(request))
                .isInstanceOf(PinNotMatchedException.class)
                .hasMessageContaining("Pin: " + "123456" + " not matched");
    }

    @Test
    void findById() throws SystemIdNotFoundException {
        // given
        String systemId = "uuid";
        System system = System.builder()
                .systemName("payment")
                .systemKey("dump")
                .build();

        SystemDTO expected = new SystemDTO(
                "uuid",
                system.getSystemName(),
                0
        );

        given(systemRepository.findById(systemId)).willReturn(Optional.of(system));
        given(systemMapper.system2systemDTO(system)).willReturn(expected);

        // when
        SystemDTO actual = underTest.findById(systemId);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByIdWillThrowSystemIdNotFound() {
        // given
        String systemId = "uuid";

        given(systemRepository.findById(systemId)).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> underTest.findById(systemId))
                .isInstanceOf(SystemIdNotFoundException.class)
                .hasMessageContaining("System Id " + systemId + " not found");
    }

    @Test
    void getSystems() {

        // when
        underTest.getSystems();

        // then
        verify(systemRepository).findAll();
    }

    @Test
    void getSystemsWillReturnListDTO() {
        // given
        System system = System.builder()
                .systemName("payment")
                .systemKey("dump")
                .numberOfWallet(0)
                .build();

        SystemDTO expected = new SystemDTO(
                "uuid",
                system.getSystemName(),
                0
        );
        given(systemRepository.findAll()).willReturn(Collections.singletonList(system));
        given(systemMapper.system2systemDTO(system)).willReturn(expected);

        // when
        List<SystemDTO> actual = underTest.getSystems();

        // then
        assertThat(actual).isEqualTo(Collections.singletonList(expected));
    }
}