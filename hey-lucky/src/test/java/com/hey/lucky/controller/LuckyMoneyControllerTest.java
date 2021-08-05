package com.hey.lucky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.service.LuckyMoneyService;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LuckyMoneyController.class)
class LuckyMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LuckyMoneyService luckyMoneyService;

    @Test
    @WithMockUser
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
        // when
        mockMvc.perform(
                post("/lucky/app/v1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());
        // then

    }

    @Test
    void testCreateLuckyMoney() {
    }

    @Test
    void getAllLuckyMoneyOfGroupChat() {
    }

    @Test
    void getDetailsLuckyMoney() {
    }
}