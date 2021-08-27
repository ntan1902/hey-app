package com.hey.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.dto.user.*;
import com.hey.auth.exception.user.PasswordNotMatchedException;
import com.hey.auth.exception.user.UserIdNotFoundException;
import com.hey.auth.service.UserService;
import com.hey.auth.utils.FileUploadUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

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

    @Mock
    private FileUploadUtil fileUploadUtil;

    private JacksonTester<ApiResponse> jsApiResponse;
    private JacksonTester<RegisterRequest> jsRegisterRequest;
    private JacksonTester<PinRequest> jsPinRequest;
    private JacksonTester<PinAmountRequest> jsPinAmountRequest;
    private JacksonTester<ChangePasswordRequest> jsChangePasswordRequest;
    private JacksonTester<ChangePinRequest> jsChangePinRequest;
    private JacksonTester<RefreshTokenRequest> jsRefreshTokenRequest;
    private JacksonTester<LogOutRequest> jsLogOutRequest;
    private JacksonTester<UpdateAvatarRequest> jsUpdateAvatarRequest;
    private JacksonTester<MockMultipartFile> jsMultipartFile;

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

    @Test
    void findUsernameByUserId() throws Exception {
        // given
        String request = "uuid";
        UsernameResponse payload = new UsernameResponse("ntan");
        given(userService.findUsernameById(request)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/users/getUsername/" + request))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void changePassword() {
        // given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setPassword("123");
        request.setConfirmPassword("123");
        request.setOldPassword("1234");

        doNothing().when(userService).changePassword(request);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Change password successfully")
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                patch("/auth/api/v1/users/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsChangePasswordRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );

    }

    @SneakyThrows
    @Test
    void changePin() {
        // given
        ChangePinRequest request = new ChangePinRequest();
        request.setPin("123456");
        request.setConfirmPin("123456");
        request.setOldPin("123457");

        doNothing().when(userService).changePin(request);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Change pin successfully")
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                patch("/auth/api/v1/users/changePin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsChangePinRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void refreshToken() {
        // given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("abc");

        RefreshTokenResponse payload = new RefreshTokenResponse("abcd", "Bearer");
        given(userService.refreshToken(request)).willReturn(payload);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload(payload)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsRefreshTokenRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void logout() {
        // given
        LogOutRequest request = new LogOutRequest();
        request.setRefreshToken("abc");

        doNothing().when(userService).logout(request);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Logout successfully")
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsLogOutRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void searchUser() {
        // given
        String request = "an";

        List<UserDTO> userDTOs = Collections.singletonList(
                UserDTO.builder()
                        .id("uuid")
                        .fullName("Trinh An")
                        .username("an")
                        .email("an@gmail.com")
                        .build()
        );
        given(userService.searchUser(request)).willReturn(userDTOs);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(userDTOs)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/users/searchUser?key=" + request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void updateAvatar() {
        // given
        UpdateAvatarRequest request = new UpdateAvatarRequest();
        request.setSmallUri("small");
        request.setMediumUri("medium");
        request.setUri("uri");

        doNothing().when(userService).updateAvatar(request);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Update avatar successfully")
                .payload(null)
                .build();

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/auth/api/v1/users/updateAvatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsUpdateAvatarRequest.write(request).getJson()))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void uploadImage() {
        // given
        MockMultipartFile request = new MockMultipartFile("file", "file.png", MediaType.IMAGE_PNG_VALUE, "a$3f5$".getBytes(StandardCharsets.UTF_8));


        UriImageDTO response = UriImageDTO.builder()
                .uri("uri")
                .mediumUri("mediumUri")
                .smallUri("smallUri")
                .build();
        given(fileUploadUtil.uploadFile(request, "/auth/api/v1/users/images/")).willReturn(response);

        ApiResponse expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Upload successfully")
                .payload(response)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                multipart("/auth/api/v1/users/uploadImage")
                        .file(request))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                jsApiResponse.write(expected).getJson()
        );
    }

    @SneakyThrows
    @Test
    void getImageWithMediaType() {
        // given
        String fileName = "file.png";

        given(fileUploadUtil.load(fileName)).willReturn("byte".getBytes(StandardCharsets.UTF_8));

        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/auth/api/v1/users/images/" + fileName))
                .andReturn().getResponse();

        // then
        assertThat(actual.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getContentAsString()).isEqualTo(
                "byte"
        );
    }
}