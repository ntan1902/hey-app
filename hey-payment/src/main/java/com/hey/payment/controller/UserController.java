package com.hey.payment.controller;

import com.hey.payment.dto.user.*;
import com.hey.payment.entity.User;
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
@Log4j2
@RequestMapping("/payment/api/v1/me")
@AllArgsConstructor
public class UserController {

    private final WalletService walletService;

    private final TransferStatementService transferStatementService;

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<Object>> getMyWallet() throws HaveNoWalletException {
        User user = getCurrentUser();
        WalletDTO walletDTO = walletService.getWalletOfUser(user.getId());
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("")
                        .payload(walletDTO)
                        .build()
        );
    }

    @PostMapping("/createTransfer")
    public ResponseEntity<ApiResponse<Object>> createTransfer(@RequestBody CreateTransferRequest createTransferRequest) throws NegativeAmountException, MaxAmountException, HaveNoWalletException, SoftTokenAuthorizeException, WrongTargetException, SourceAndTargetAreTheSameException, WrongSourceException, BalanceNotEnoughException, MaxBalanceException {
        User user = getCurrentUser();
        transferStatementService.createTransfer(user, createTransferRequest);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("Transfer successfully")
                        .payload("")
                        .build()
        );
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<Object>> topUp(@RequestBody TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException {
        User user = getCurrentUser();
        transferStatementService.topUp(user, topupRequest);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("TopUp successfully")
                        .payload("")
                        .build()
        );
    }


    @GetMapping("/getTransferStatement")
    public ResponseEntity<ApiResponse<Object>> getTransferStatement() throws DatabaseHasErr, HaveNoWalletException, ApiErrException {
        User user = getCurrentUser();
        List<TransferStatementDTO> transferStatementDTOList = transferStatementService.getTransferStatementOfUser(user.getId());
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("")
                        .payload(transferStatementDTOList).build()
        );
    }

    @PostMapping("/createWallet")
    public ResponseEntity<ApiResponse<Object>> createWallet() throws HadWalletException {
        User user = getCurrentUser();
        WalletDTO walletDTO = walletService.createWallet(user);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("Create wallet successfully")
                        .payload(walletDTO).build()
        );
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
