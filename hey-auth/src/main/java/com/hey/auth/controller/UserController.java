package com.hey.auth.controller;

import com.hey.auth.dto.api.*;
import com.hey.auth.dto.user.*;
import com.hey.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth/api/v1/users")
@AllArgsConstructor
@Log4j2
@CrossOrigin("http://localhost:3000")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload("Register successfully")
                .build());
    }

    @GetMapping("/getInfo")
    public ResponseEntity<ApiResponse> getInfo() {
        UserDTO payload = userService.findById();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/createPin")
    public ResponseEntity<ApiResponse> createPin(@RequestBody @Valid PinRequest pinRequest) {
        userService.createPin(pinRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Create PIN successfully")
                .payload("")
                .build());
    }

    @GetMapping("/hasPin")
    public ResponseEntity<ApiResponse> hasPin() {
        HasPinResponse payload = userService.hasPin();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Create PIN successfully")
                .payload(payload)
                .build());
    }

    @PostMapping("/createSoftTokenByPin")
    public ResponseEntity<ApiResponse> createSoftToken(@RequestBody @Valid PinAmountRequest pinAmountRequest) {
        SoftTokenResponse payload = userService.createSoftToken(pinAmountRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload(payload)
                .build());
    }

}
