package com.hey.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserResponse;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.exception_handler.exception.*;
import com.hey.payment.service.TransferStatementService;
import com.hey.payment.service.WalletService;
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
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class SystemControllerTest {

    private MockMvc mockMvc;


    @InjectMocks
    private SystemController underTest;

    @Mock
    private WalletService walletService;

    @Mock
    private TransferStatementService transferStatementService;

    private JacksonTester<ApiResponse> jsApiResponse;
    private JacksonTester<SystemCreateTransferToUserRequest> jsSystemCreateTransferToUserRequest;
    private JacksonTester<SystemCreateTransferFromUserRequest> jsSystemCreateTransferFromUserRequest;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(underTest)
                .build();
    }

    @Test
    void getAllWalletOfSystem() throws Exception {
        // given
        WalletSystemDTO wallet1 = WalletSystemDTO.builder()
                .walletId(100L)
                .balance(200000L)
                .build();

        WalletSystemDTO wallet2 = WalletSystemDTO.builder()
                .walletId(100L)
                .balance(200000L)
                .build();
        List<WalletSystemDTO> walletSystemDTOList = new ArrayList<>();
        walletSystemDTOList.add(wallet1);
        walletSystemDTOList.add(wallet2);
        when(walletService.findAllWalletsOfSystem()).thenReturn(walletSystemDTOList);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(walletSystemDTOList).build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/payment/api/v1/systems/getAllWallets")
        ).andReturn().getResponse();
        // then
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void createTransferToUser() throws Exception {
        // given
        SystemCreateTransferToUserRequest request = SystemCreateTransferToUserRequest.builder()
                .walletId(123L)
                .receiverId("abc-123")
                .amount(2000L)
                .message("Hello").build();
        doNothing().when(transferStatementService).systemCreateTransferToUser(request);
        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SystemController.SYSTEM_TRANSFER_TO_USER_SUCCESSFULLY)
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/payment/api/v1/systems/createTransferToUser/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsSystemCreateTransferToUserRequest.write(request).getJson())
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void createTransferFromUser() throws Exception {
        // given
        SystemCreateTransferFromUserRequest request = SystemCreateTransferFromUserRequest.builder()
                .userId("123-abc")
                .walletId(123L)
                .message("Cho nè")
                .softToken("123-123-abc-abc")
                .build();
        SystemCreateTransferFromUserResponse payload = new SystemCreateTransferFromUserResponse(1300L);
        when(transferStatementService.systemCreateTransferFromUser(request)).thenReturn(payload);
        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SystemController.SYSTEM_TRANSFER_FROM_USER_SUCCESSFULLY)
                .payload(payload).build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/payment/api/v1/systems/createTransferFromUser/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsSystemCreateTransferFromUserRequest.write(request).getJson())
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }
}