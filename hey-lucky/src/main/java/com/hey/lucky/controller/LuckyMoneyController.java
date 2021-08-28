package com.hey.lucky.controller;

import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.LuckyMoneyDetails;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.service.LuckyMoneyService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lucky/api/v1")
@AllArgsConstructor
@Log4j2
public class LuckyMoneyController {
    public static final String SENT_LUCKY_MONEY = "Sent lucky money";
    public static final String RECEIVED_LUCKY_MONEY = "Received lucky money";

    private final LuckyMoneyService luckyMoneyService;

    @PostMapping("/createLuckyMoney")
    public ResponseEntity<ApiResponse<Object>> createLuckyMoney(@RequestBody CreateLuckyMoneyRequest createLuckyMoneyRequest) throws InternalServerErrException, ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UserNotInSessionChatException, MinAmountPerBagException {
        luckyMoneyService.createLuckyMoney(createLuckyMoneyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SENT_LUCKY_MONEY)
                .payload("")
                .build());
    }
    @PostMapping("/receiveLuckyMoney")
    public ResponseEntity<ApiResponse<Object>> receiveLuckyMoney(@RequestBody ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException, InternalServerErrException, ErrCallApiException, LuckyMoneyExpiredException, OutOfBagException, CannotTransferMoneyException, HadReceivedException, ErrCallChatApiException, UserNotInSessionChatException {
        log.info("Receive lucky money");
        luckyMoneyService.receiveLuckyMoney(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(RECEIVED_LUCKY_MONEY)
                .payload("")
                .build());
    }
    @GetMapping("/getAllLuckyMoney")
    public ResponseEntity<ApiResponse<Object>> getAllLuckyMoneyOfGroupChat(@RequestParam String sessionId) throws InternalServerErrException, ErrCallApiException, CannotGetUserInfo, UserNotInSessionChatException {
        log.info("Get all lucky money");
        List<LuckyMoneyDTO> luckyMoneyDTOList = luckyMoneyService.getAllLuckyMoneyOfSession(sessionId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(luckyMoneyDTOList)
                .build());
    }
    @GetMapping("/getDetailsLuckyMoney")
    public ResponseEntity<ApiResponse<Object>> getDetailsLuckyMoney(@RequestParam long luckyMoneyId) throws InvalidLuckyMoneyException, InternalServerErrException, ErrCallApiException, CannotGetUserInfo, UserNotInSessionChatException {
        LuckyMoneyDetails luckyMoneyDetails = luckyMoneyService.getLuckyMoneyDetails(luckyMoneyId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(luckyMoneyDetails)
                .build());
    }
}
