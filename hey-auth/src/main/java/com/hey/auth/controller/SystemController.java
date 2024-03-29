package com.hey.auth.controller;

import com.hey.auth.dto.api.ApiResponse;
import com.hey.auth.dto.system.*;
import com.hey.auth.dto.user.EditUserRequest;
import com.hey.auth.dto.user.UserDTO;
import com.hey.auth.exception.jwt.InvalidJwtTokenException;
import com.hey.auth.exception.system.InvalidSoftTokenException;
import com.hey.auth.exception.system.SystemIdNotFoundException;
import com.hey.auth.exception.system.SystemKeyInvalidException;
import com.hey.auth.exception.user.PinNotMatchedException;
import com.hey.auth.exception.user.UserIdNotFoundException;
import com.hey.auth.exception.user.UsernameEmailExistedException;
import com.hey.auth.service.SystemService;
import com.hey.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth/api/v1/systems")
@AllArgsConstructor
@Log4j2
@CrossOrigin({"http://localhost:9090", "http://localhost:8080", "http://localhost:6060"})
public class SystemController {
    private final SystemService systemService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid SystemLoginRequest loginRequest) throws SystemKeyInvalidException {

        SystemLoginResponse payload = systemService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/authorizeUser")
    public ResponseEntity<ApiResponse> authorizeUser(@RequestBody @Valid AuthorizeRequest authorizeRequest) throws InvalidJwtTokenException, UserIdNotFoundException {
        AuthorizeResponse payload = systemService.authorizeUser(authorizeRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/authorizeSystem")
    public ResponseEntity<ApiResponse> authorizeSystem(@RequestBody @Valid SystemAuthorizeRequest authorizeRequest) throws InvalidJwtTokenException, SystemIdNotFoundException {
        SystemAuthorizeResponse payload = systemService.authorizeSystem(authorizeRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @PostMapping("/authorizeSoftToken")
    public ResponseEntity<ApiResponse> authorizeSoftToken(@RequestBody @Valid SoftTokenRequest softTokenRequest) throws PinNotMatchedException, InvalidJwtTokenException, UserIdNotFoundException, InvalidSoftTokenException {
        UserIdAmountResponse payload = systemService.authorizeSoftToken(softTokenRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Authorize soft token successfully")
                .payload(payload)
                .build());
    }

    @GetMapping("/getSystemInfo/{systemId}")
    public ResponseEntity<ApiResponse> getSystemInfo(@PathVariable("systemId") String systemId) throws SystemIdNotFoundException {
        SystemDTO payload = systemService.findById(systemId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @GetMapping("/getUserInfo/{userId}")
    public ResponseEntity<ApiResponse> getUserInfo(@PathVariable("userId") String userId) throws UserIdNotFoundException {
        UserDTO payload = userService.findById(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }

    @GetMapping("/getSystems")
    public ResponseEntity<ApiResponse> getSystems() {
        List<SystemDTO> payload = systemService.getSystems();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }


    @PatchMapping("/editProfile/{userId}")
    public ResponseEntity<ApiResponse> editUser(
            @PathVariable("userId") String userId,
            @RequestBody @Valid EditUserRequest request) throws UsernameEmailExistedException, UserIdNotFoundException {
        userService.editUser(userId, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .message("Edit profile successfully")
                .payload("")
                .build());
    }
}
