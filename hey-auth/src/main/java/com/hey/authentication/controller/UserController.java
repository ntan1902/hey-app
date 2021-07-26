package com.hey.authentication.controller;

import com.hey.authentication.dto.api.*;
import com.hey.authentication.dto.user.*;
import com.hey.authentication.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Log4j2
@CrossOrigin("http://localhost:3000")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse payload = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
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
    public ResponseEntity<ApiResponse> createPin(@RequestBody PinAmountRequest pinAmountRequest) {
        userService.createPin(pinAmountRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Create PIN successfully")
                .payload("")
                .build());
    }

    @PostMapping("/hasPin")
    public ResponseEntity<ApiResponse> hasPin() {
        HasPinResponse payload = userService.hasPin();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("Create PIN successfully")
                .payload("")
                .build());
    }

    @PostMapping("/createSoftTokenByPin")
    public ResponseEntity<ApiResponse> createSoftToken(@RequestBody PinAmountRequest pinAmountRequest) {
        SoftTokenResponse payload = userService.createSoftToken(pinAmountRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("")
                .payload(payload)
                .build());
    }

}
