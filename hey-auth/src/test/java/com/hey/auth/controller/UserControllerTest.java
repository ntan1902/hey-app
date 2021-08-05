package com.hey.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.dto.user.*;
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


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private UserController underTest;

    @Mock
    private UserService userService;

    private JacksonTester<ApiResponse> jsApiResponse;
    private JacksonTester<RegisterRequest> jsRegisterRequest;
    private JacksonTester<PinRequest> jsPinRequest;
    private JacksonTester<PinAmountRequest> jsPinAmountRequest;


    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());

        mockMvc = MockMvcBuilders.standaloneSetup(underTest)
                .build();
    }

    @Test
    void register() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest("ntan", "123", "ntan@gmail.com", "Trinh An");

        doNothing().when(userService).register(registerRequest);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload("Register successfully")
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsRegisterRequest.write(registerRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void registerEmptyUsername() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest("", "123", "ntan@gmail.com", "Trinh An");

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsRegisterRequest.write(registerRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void registerEmptyPassword() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest("ntan", "", "ntan@gmail.com", "Trinh An");

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsRegisterRequest.write(registerRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void registerNotValidEmail() throws Exception {
        // given
        RegisterRequest registerRequest = new RegisterRequest("ntan", "123", "ntangmail.com", "Trinh An");

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsRegisterRequest.write(registerRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void getInfo() throws Exception {
        // given
        UserDTO payload = UserDTO.builder()
                .id("uuid")
                .email("ntan@gmail.com")
                .username("ntan")
                .fullName("Trinh An")
                .build();
        given(userService.findById()).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/users/getInfo"))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void createPin() throws Exception {
        // given
        PinRequest request = new PinRequest("123456");
        doNothing().when(userService).createPin(request);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Create PIN successfully")
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/createPin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsPinRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void hasPin() throws Exception {
        // given
        HasPinResponse payload = new HasPinResponse(true);

        given(userService.hasPin()).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/users/hasPin"))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @Test
    void createSoftToken() throws Exception {
        // given
        PinAmountRequest request = new PinAmountRequest("123456", 5000L);
        SoftTokenResponse payload = new SoftTokenResponse("1232132131fdf");

        given(userService.createSoftToken(request)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload(payload)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/createSoftTokenByPin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsPinAmountRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );

    }
}