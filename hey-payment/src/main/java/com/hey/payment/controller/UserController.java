package com.hey.payment.controller;

import com.hey.payment.dto.user.*;
import com.hey.payment.entity.User;
import com.hey.payment.service.TransferStatementService;
import com.hey.payment.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/api/v1/me")
@AllArgsConstructor
public class UserController {

    private final WalletService walletService;

    private final TransferStatementService transferStatementService;

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<?>> getMyWallet(){
        User user = getCurrentUser();
        WalletDTO walletDTO = walletService.getWalletOfUser(user.getId());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .payload(walletDTO)
                .build());
    }

    @PostMapping("/createTransfer")
    public ResponseEntity<ApiResponse<?>> createTransfer(@RequestBody CreateTransferRequest createTransferRequest){
        User user = getCurrentUser();
        transferStatementService.createTransfer(user, createTransferRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Transfer success").build());
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<?>> topup(@RequestBody TopupRequest topupRequest){
        User user = getCurrentUser();
        transferStatementService.topUp(user,topupRequest);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Topup successfully")
                .payload("")
                .build());
    }


    @PostMapping("/getTransferStatement")
    public ResponseEntity<ApiResponse<?>> getTransferStatement(){
        User user = getCurrentUser();
        List<TransferStatementDTO> transferStatementDTOList = transferStatementService.getTransferStatementOfUser(user.getId());
        return null;
    }

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
