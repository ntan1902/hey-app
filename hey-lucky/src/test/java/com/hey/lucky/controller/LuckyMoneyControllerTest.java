package com.hey.lucky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.LuckyMoneyDetails;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import com.hey.lucky.exception_handler.handler.MyExceptionHandler;
import com.hey.lucky.service.LuckyMoneyService;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class LuckyMoneyControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private LuckyMoneyService luckyMoneyService;

    @InjectMocks
    private LuckyMoneyController luckyMoneyController;

    private JacksonTester<CreateLuckyMoneyRequest> jsCreateLuckyMoneyRequest;
    private JacksonTester<ReceiveLuckyMoneyRequest> jsReceiveLuckyMoneyRequest;
    private JacksonTester<ApiResponse> jsApiResponse;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(luckyMoneyController)
                .setControllerAdvice(new MyExceptionHandler())
                .build();
    }

    @Test
    void createLuckyMoney_return200() throws Exception {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .softToken("abc-123-7812dd")
                .message("Chuc mung nam moi")
                .sessionChatId("abc-123")
                .numberBag(10)
                .type(TypeLuckyMoney.RANDOM)
                .build();
        doNothing().when(luckyMoneyService).createLuckyMoney(request);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(LuckyMoneyController.SENT_LUCKY_MONEY)
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/lucky/api/v1/createLuckyMoney/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsCreateLuckyMoneyRequest.write(request).getJson())
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(), actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(), actual.getContentAsString());
    }

    //    UnauthorizeException, ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException
    @Test
    void createLuckyMoney_returnErr() throws Exception {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .softToken("abc-123-7812dd")
                .message("Chuc mung nam moi")
                .sessionChatId("abc-123")
                .numberBag(10)
                .type(TypeLuckyMoney.RANDOM)
                .build();
        doThrow(new ErrCallApiException("Error")).when(luckyMoneyService).createLuckyMoney(request);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(false)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Error")
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/lucky/api/v1/createLuckyMoney/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsCreateLuckyMoneyRequest.write(request).getJson())
        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(), actual.getContentAsString());
    }

    @Test
    void receiveLuckyMoney() throws Exception {
        // given
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(123L).build();
        doNothing().when(luckyMoneyService).receiveLuckyMoney(request);

        ApiResponse<Object> expected  = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(LuckyMoneyController.RECEIVED_LUCKY_MONEY)
                .payload("")
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                post("/lucky/api/v1/receiveLuckyMoney")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsReceiveLuckyMoneyRequest.write(request).getJson())

        ).andReturn().getResponse();
        // then
        assertEquals(HttpStatus.OK.value(),actual.getStatus());
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void getAllLuckyMoneyOfGroupChat() throws Exception {
        // given
        String sessionId = "abc-123";
        LuckyMoneyDTO luckyMoneyDTO1 = LuckyMoneyDTO.builder()
                .luckyMoneyId(123L)
                .build();

        LuckyMoneyDTO luckyMoneyDTO2 = LuckyMoneyDTO.builder()
                .luckyMoneyId(1234L)
                .build();
        List<LuckyMoneyDTO> luckyMoneyDTOList = new ArrayList<>();
        luckyMoneyDTOList.add(luckyMoneyDTO1);
        luckyMoneyDTOList.add(luckyMoneyDTO2);

        when(luckyMoneyService.getAllLuckyMoneyOfSession(sessionId)).thenReturn(luckyMoneyDTOList);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(luckyMoneyDTOList)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/lucky/api/v1/getAllLuckyMoney?sessionId="+sessionId)
        ).andReturn().getResponse();
        // then
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }

    @Test
    void getDetailsLuckyMoney() throws Exception {
        // given
        long luckyMoneyId = 123L;
        LuckyMoneyDetails luckyMoneyDetails = LuckyMoneyDetails.builder()
                .restMoney(5000L)
                .restBag(4)
                .type(TypeLuckyMoney.RANDOM)
                .build();

        when(luckyMoneyService.getLuckyMoneyDetails(luckyMoneyId)).thenReturn(luckyMoneyDetails);

        ApiResponse<Object> expected = ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(luckyMoneyDetails)
                .build();
        // when
        MockHttpServletResponse actual = mockMvc.perform(
                get("/lucky/api/v1/getDetailsLuckyMoney?luckyMoneyId="+luckyMoneyId)
        ).andReturn().getResponse();
        // then
        assertEquals(jsApiResponse.write(expected).getJson(),actual.getContentAsString());
    }
}