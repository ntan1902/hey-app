package com.hey.lucky.controller;

import com.hey.lucky.dto.ApiResponse;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;
import com.hey.lucky.service.LuckyMoneyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class LuckyMoneyController {
    private final LuckyMoneyService luckyMoneyService;

    @PostMapping("/createLuckyMoney")
    public ResponseEntity<ApiResponse<?>> createLuckyMoney(@RequestBody CreateLuckyMoneyRequest createLuckyMoneyRequest){
        luckyMoneyService.createLuckyMoney(createLuckyMoneyRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Sent lucky money")
                .build());
    }
    @PostMapping("/receiveLuckyMoney")
    public ResponseEntity<ApiResponse<?>> createLuckyMoney(@RequestBody ReceiveLuckyMoneyRequest request){
        luckyMoneyService.receiveLuckyMoney(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Received lucky money")
                .payload("")
                .build());
    }
}
