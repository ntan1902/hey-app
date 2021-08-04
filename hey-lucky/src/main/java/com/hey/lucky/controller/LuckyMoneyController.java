package com.hey.lucky.controller;

import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.dto.user.*;
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
    private final LuckyMoneyService luckyMoneyService;

    @PostMapping("/createLuckyMoney")
    public ResponseEntity<ApiResponse<?>> createLuckyMoney(@RequestBody CreateLuckyMoneyRequest createLuckyMoneyRequest) throws UnauthorizeException, ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException {
        luckyMoneyService.createLuckyMoney(createLuckyMoneyRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Sent lucky money")
                .build());
    }
    @PostMapping("/receiveLuckyMoney")
    public ResponseEntity<ApiResponse<?>> createLuckyMoney(@RequestBody ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException, UnauthorizeException, ErrCallApiException, LuckyMoneyExpiredException, OutOfBagException, CannotTransferMoneyException, HadReceivedException, ErrCallChatApiException {
        log.info("Receive lucky money");
        luckyMoneyService.receiveLuckyMoney(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Received lucky money")
                .payload("")
                .build());
    }
    @GetMapping("/getAllLuckyMoney")
    public ResponseEntity<ApiResponse<?>> getAllLuckyMoneyOfGroupChat(@RequestParam String sessionId) throws UnauthorizeException, ErrCallApiException {
        log.info("Get all lucky money");
        List<LuckyMoneyDTO> luckyMoneyDTOList = luckyMoneyService.getAllLuckyMoneyOfSession(sessionId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(luckyMoneyDTOList).build());
    }
    @GetMapping("/getDetailsLuckyMoney")
    public ResponseEntity<ApiResponse<?>> getDetailsLuckyMoney(@RequestParam long luckyMoneyId) throws InvalidLuckyMoneyException, UnauthorizeException, ErrCallApiException, CannotGetUserInfo {
        LuckyMoneyDetails luckyMoneyDetails = luckyMoneyService.getLuckyMoneyDetails(luckyMoneyId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(luckyMoneyDetails).build());
    }
}
