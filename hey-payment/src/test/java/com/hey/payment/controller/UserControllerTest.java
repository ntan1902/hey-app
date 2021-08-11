package com.hey.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hey.payment.constant.TransferStatus;
import com.hey.payment.dto.user.*;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private WalletService walletService;

    @Mock
    private TransferStatementService transferStatementService;

    private JacksonTester<ApiResponse> jsApiResponse;
    private JacksonTester<CreateTransferRequest> jsCreateTransferRequest;
    private JacksonTester<TopUpRequest> jsTopUpRequest;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper().registerModule(new JavaTimeModule()));
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getMyWallet() throws Exception {
        // given
        WalletDTO walletDTO = WalletDTO.builder()
                .balance(123000L)
                .build();
        when(walletService.findWalletOfUser()).thenReturn(walletDTO);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(walletDTO).build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/payment/api/v1/me/wallet/")
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(), actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(), actual.getContentAsString());
    }

    @Test
    void createTransfer() throws Exception {
        // given
        CreateTransferRequest request = CreateTransferRequest.builder()
                .targetId("123-abc")
                .softToken("123-a23-adsf1")
                .message("Cho ne")
                .build();
        doNothing().when(transferStatementService).createTransfer(request);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(UserController.USER_TRANSFER_SUCCESSFULLY)
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/payment/api/v1/me/createTransfer/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsCreateTransferRequest.write(request).getJson())
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void topUp() throws Exception {
        // given
        TopUpRequest request = TopUpRequest.builder()
                .bankId("123-abc")
                .amount(123000L)
                .build();
        doNothing().when(transferStatementService).topUp(request);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(UserController.TOP_UP_SUCCESSFULLY)
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/payment/api/v1/me/topup/")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsTopUpRequest.write(request).getJson())
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void getTransferStatement() throws Exception {
        // given
        int page = 1;
        int size = 10;

        TransferStatementDTO ts1 = TransferStatementDTO.builder()
                .amount(123L)
                .description("Cho ne")
                .status(TransferStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
        TransferStatementDTO ts2 = TransferStatementDTO.builder()
                .amount(123L)
                .description("Cho ne")
                .status(TransferStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
        List<TransferStatementDTO> transferStatementDTOList = new ArrayList<>();
        transferStatementDTOList.add(ts1);
        transferStatementDTOList.add(ts2);

        when(transferStatementService.getTransferStatementOfUser(page,size)).thenReturn(transferStatementDTOList);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(transferStatementDTOList)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get(String.format( "/payment/api/v1/me/getTransferStatement?page=%d&size=%d", page,size))
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void createWallet() throws Exception {
        // given
        WalletDTO walletDTO = WalletDTO.builder()
                .balance(123000L)
                .build();
        when(walletService.createWallet()).thenReturn(walletDTO);
        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(UserController.CREATE_WALLET_SUCCESSFULLY)
                .payload(walletDTO)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/payment/api/v1/me/createWallet")
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void hasWallet() throws Exception {
        // given
        HasWalletResponse payload = new HasWalletResponse(true);

        when(walletService.hasWallet()).thenReturn(payload);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/payment/api/v1/me/hasWallet")
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }
}