package com.hey.payment.controller;

import com.hey.payment.dto.user.*;
import com.hey.payment.exception_handler.exception.*;
import com.hey.payment.service.TransferStatementService;
import com.hey.payment.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/payment/api/v1/me")
@AllArgsConstructor
public class UserController {

    public static final String USER_TRANSFER_SUCCESSFULLY = "Transfer successfully";
    public static final String TOP_UP_SUCCESSFULLY = "TopUp successfully";
    public static final String CREATE_WALLET_SUCCESSFULLY = "Create wallet successfully";
    private final WalletService walletService;

    private final TransferStatementService transferStatementService;

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<Object>> getMyWallet() throws HaveNoWalletException {
        WalletDTO walletDTO = walletService.findWalletOfUser();
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
    public ResponseEntity<ApiResponse<Object>> createTransfer(@RequestBody CreateTransferRequest createTransferRequest) throws MinAmountException, MaxAmountException, HaveNoWalletException, SoftTokenAuthorizeException, SourceAndTargetAreTheSameException, BalanceNotEnoughException, MaxBalanceException {
        transferStatementService.createTransfer(createTransferRequest);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message(USER_TRANSFER_SUCCESSFULLY)
                        .payload("")
                        .build()
        );
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<Object>> topUp(@RequestBody TopUpRequest topupRequest) throws MaxAmountException, MaxBalanceException, HaveNoWalletException, BankInvalidException {
        transferStatementService.topUp(topupRequest);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message(TOP_UP_SUCCESSFULLY)
                        .payload("")
                        .build()
        );
    }


    @SneakyThrows
    @GetMapping("/getTransferStatements")
    public ResponseEntity<ApiResponse<Object>> getTransferStatement(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<TransferStatementDTO> transferStatementDTOList = transferStatementService.getTransferStatementsOfUser(offset, limit);
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
        WalletDTO walletDTO = walletService.createWallet();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message(CREATE_WALLET_SUCCESSFULLY)
                        .payload(walletDTO).build()
        );
    }

    @GetMapping("/hasWallet")
    public ResponseEntity<ApiResponse<Object>> hasWallet() {
        HasWalletResponse payload = walletService.hasWallet();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("")
                .payload(payload)
                .build());
    }


}
