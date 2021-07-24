package com.hey.payment.controller;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.entity.System;
import com.hey.payment.service.TransferStatementService;
import com.hey.payment.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/systems")
@AllArgsConstructor
@Log4j2
public class SystemController {

    private final WalletService walletService;

    private final TransferStatementService transferStatementService;

    @GetMapping("/getAllWallets")
    public ResponseEntity<ApiResponse> getAllWalletOfSystem() {
        System system = getCurrentSystem();
        log.info("System {} getAllWalletOfSystem", system.getId());
        List<WalletSystemDTO> walletSystemDTOList = walletService.getAllWalletOfSystem(system);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .payload(walletSystemDTOList)
                .build());
    }

    @PostMapping("/createTransferToUser")
    public ResponseEntity<ApiResponse> createTransferToUser(SystemCreateTransferToUserRequest request) {
        System system = getCurrentSystem();
        log.info("System {} createTransferToUser", system.getId());
        transferStatementService.systemCreateTransferToUser(system, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .payload("")
                .build());
    }

    @PostMapping("/createTransferFromUser")
    public ResponseEntity<ApiResponse> createTransferFromUser(SystemCreateTransferFromUserRequest request) {
        System system = getCurrentSystem();
        log.info("System {} createTransferFromUser", system.getId());
        transferStatementService.systemCreateTransferFromUser(system, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .payload("")
                .build());
    }

    private System getCurrentSystem() {
        return (System) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
