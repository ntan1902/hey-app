package com.hey.payment.controller;

import com.hey.payment.dto.system.SystemCreateTransferFromUserRequest;
import com.hey.payment.dto.system.SystemCreateTransferFromUserResponse;
import com.hey.payment.dto.system.SystemCreateTransferToUserRequest;
import com.hey.payment.dto.system.WalletSystemDTO;
import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.exception_handler.exception.*;
import com.hey.payment.service.TransferStatementService;
import com.hey.payment.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment/api/v1/systems")
@AllArgsConstructor
@Log4j2
public class SystemController {

    public static final String SYSTEM_TRANSFER_TO_USER_SUCCESSFULLY = "Transfer successfully";
    public static final String SYSTEM_TRANSFER_FROM_USER_SUCCESSFULLY = "Transfer successfully";
    private final WalletService walletService;

    private final TransferStatementService transferStatementService;

    @GetMapping("/getAllWallets")
    public ResponseEntity<ApiResponse<Object>> getAllWalletOfSystem() {
        log.info("System getAllWalletOfSystem");
        List<WalletSystemDTO> walletSystemDTOList = walletService.findAllWalletsOfSystem();
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message("")
                        .payload(walletSystemDTOList)
                        .build()
        );
    }

    @PostMapping("/createTransferToUser")
    public ResponseEntity<ApiResponse<Object>> createTransferToUser(@RequestBody SystemCreateTransferToUserRequest request) throws MinAmountException, MaxAmountException, HaveNoWalletException, BalanceNotEnoughException, MaxBalanceException {

        log.info("System createTransferToUser");
        transferStatementService.systemCreateTransferToUser(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message(SYSTEM_TRANSFER_TO_USER_SUCCESSFULLY)
                        .payload("")
                        .build()
        );
    }

    @PostMapping("/createTransferFromUser")
    public ResponseEntity<ApiResponse<Object>> createTransferFromUser(@RequestBody SystemCreateTransferFromUserRequest request) throws MinAmountException, MaxAmountException, SoftTokenAuthorizeException, BalanceNotEnoughException, MaxBalanceException, HaveNoWalletException {
        log.info("System createTransferFromUser");
        SystemCreateTransferFromUserResponse payload = transferStatementService.systemCreateTransferFromUser(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .code(HttpStatus.OK.value())
                        .message(SYSTEM_TRANSFER_FROM_USER_SUCCESSFULLY)
                        .payload(payload)
                        .build()
        );
    }

}
