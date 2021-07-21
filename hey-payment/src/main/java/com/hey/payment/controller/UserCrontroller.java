package com.hey.payment.controller;

import com.hey.payment.dto.user.ApiResponse;
import com.hey.payment.dto.user.WalletDTO;
import com.hey.payment.entity.User;
import com.hey.payment.service.WalletService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/api/v1/me")
public class UserCrontroller {

    @Autowired
    private WalletService walletService;

    @GetMapping("/")
    public String testFilter(){
        return "abc";
    }
    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse> getMyWallet(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        WalletDTO walletDTO = walletService.getWalletOfUser(user.getId());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .payload(walletDTO)
                .build());
    }
}
