package com.hey.authentication.controller;

import com.hey.authentication.dto.LoginRequest;
import com.hey.authentication.dto.LoginResponse;
import com.hey.authentication.dto.RegisterRequest;
import com.hey.authentication.dto.UserDTO;
import com.hey.authentication.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Log4j2
@CrossOrigin("http://localhost:3000")
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserDTO res = userService.register(registerRequest);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

}
