package com.hey.authentication.controller;

import com.hey.authentication.dto.api.ApiResponse;
import com.hey.authentication.dto.system.*;
import com.hey.authentication.service.SystemService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/systems")
@AllArgsConstructor
@Log4j2
@CrossOrigin({"http://localhost:9090", "http://localhost:8080", "http://localhost:6060"})
public class SystemController {
    private final SystemService systemService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login( @RequestBody SystemLoginRequest loginRequest) {

        SystemLoginResponse payload = systemService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/authorizeUser")
    public ResponseEntity<ApiResponse> authorizeUser(@RequestBody AuthorizeRequest authorizeRequest) {
        AuthorizeResponse payload = systemService.authorizeUser(authorizeRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/authorizeSystem")
    public ResponseEntity<ApiResponse> authorizeSystem(@RequestBody SystemAuthorizeRequest authorizeRequest) {
        SystemAuthorizeResponse payload = systemService.authorizeSystem(authorizeRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

}
