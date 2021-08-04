package com.hey.payment.controller;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.entity.System;
import com.hey.payment.exception_handler.exception.*;
import com.hey.payment.service.TransferStatementService;
import com.hey.payment.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment/api/v1/systems")
@AllArgsConstructor
@Log4j2
public class SystemController {

    private final WalletService walletService;

    private final TransferStatementService transferStatementService;

    @GetMapping("/getAllWallets")
    public ResponseEntity<ApiResponse<?>> getAllWalletOfSystem() {
        System system = getCurrentSystem();
        log.info("System {} getAllWalletOfSystem", system.getId());
        List<WalletSystemDTO> walletSystemDTOList = walletService.getAllWalletsOfSystem(system);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .payload(walletSystemDTOList)
                .build());
    }

    @PostMapping("/createTransferToUser")
    public ResponseEntity<ApiResponse<?>> createTransferToUser(@RequestBody SystemCreateTransferToUserRequest request) throws NegativeAmountException, WrongSourceException, MaxAmountException, HaveNoWalletException {
        System system = getCurrentSystem();
        log.info("System {} createTransferToUser", system.getId());
        return ResponseEntity.ok(transferStatementService.systemCreateTransferToUser(system, request));
    }

    @PostMapping("/createTransferFromUser")
    public ResponseEntity<ApiResponse<?>> createTransferFromUser(@RequestBody SystemCreateTransferFromUserRequest request) throws NegativeAmountException, WrongSourceException, MaxAmountException, SoftTokenAuthorizeException, WrongTargetException {
        System system = getCurrentSystem();
        log.info("System {} createTransferFromUser", system.getId());
        return ResponseEntity.ok(transferStatementService.systemCreateTransferFromUser(system, request));
    }

    private System getCurrentSystem() {
        return (System) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
