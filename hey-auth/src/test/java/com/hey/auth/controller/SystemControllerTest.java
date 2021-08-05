package com.hey.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.dto.system.*;
import com.hey.auth.dto.user.UserDTO;
import com.hey.auth.service.SystemService;
import com.hey.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(MockitoExtension.class)
class SystemControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private SystemController underTest;

    @Mock
    private SystemService systemService;
    @Mock
    private UserService userService;

    private JacksonTester<SystemLoginRequest> jsLoginRequest;
    private JacksonTester<AuthorizeRequest> jsAuthorizeRequest;
    private JacksonTester<SystemAuthorizeRequest> jsSystemAuthorizeRequest;
    private JacksonTester<SoftTokenRequest> jsSoftTokenRequest;
    private JacksonTester<ApiResponse> jsApiResponse;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(underTest)
                .build();
    }

    @Test
    void login() throws Exception {
        // given
        SystemLoginRequest request = new SystemLoginRequest("payment", "123456");

        SystemLoginResponse payload = new SystemLoginResponse(
                "dump",
                "Bearer"
        );
        given(systemService.login(request)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/systems/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsLoginRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void authorizeUser() throws Exception {
        // given
        AuthorizeRequest softTokenRequest = new AuthorizeRequest(
                "dump"
        );
        AuthorizeResponse payload = new AuthorizeResponse(
                "uuid"
        );
        given(systemService.authorizeUser(softTokenRequest)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/systems/authorizeUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsAuthorizeRequest.write(softTokenRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void authorizeSystem() throws Exception {
        // given
        SystemAuthorizeRequest authorizeRequest = new SystemAuthorizeRequest(
                "dump"
        );
        SystemAuthorizeResponse payload = new SystemAuthorizeResponse(
                "uuid",
                "payment"
        );
        given(systemService.authorizeSystem(authorizeRequest)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/systems/authorizeSystem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsSystemAuthorizeRequest.write(authorizeRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void authorizeSoftToken() throws Exception {
        // given
        SoftTokenRequest softTokenRequest = new SoftTokenRequest(
                "dump"
        );
        UserIdAmountResponse payload = new UserIdAmountResponse(
                "uuid",
                50000L
        );
        given(systemService.authorizeSoftToken(softTokenRequest)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Authorize soft token successfully")
                .payload(payload)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/systems/authorizeSoftToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsSoftTokenRequest.write(softTokenRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void getUserInfo() throws Exception {
        // given
        String userId = "uuid";

        UserDTO payload = UserDTO.builder()
                .id(userId)
                .username("ntan1902")
                .fullName("Trinh An")
                .email("ntan1902@gmail.com")
                .build();

        given(userService.findById(userId)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/systems/getUserInfo/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
            jsApiResponse.write(expected).getJson()
        );
    }
    @Test
    void getSystemInfo() throws Exception {
        // given
        String systemId = "uuid";

        SystemDTO payload = new SystemDTO(systemId, "payment", 0);

        given(systemService.findById(systemId)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/systems/getSystemInfo/" + systemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }
}