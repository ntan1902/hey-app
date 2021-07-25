package com.hey.lucky.service;

import com.hey.lucky.api.ChatApi;
import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;
import com.hey.lucky.dto.payment_service.CreateTransferFromUserRequest;
import com.hey.lucky.dto.payment_service.CreateTransferFromUserResponse;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;
import com.hey.lucky.repository.LuckyMoneyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class LuckyMoneyServiceImpl implements LuckyMoneyService {
    private static AtomicInteger count = new AtomicInteger(0);
    private static List<Long> walletIds = new ArrayList<>();
    private static int numberWallet;

    private final LuckyMoneyRepository luckyMoneyRepository;
    private final PaymentApi paymentApi;
    private final ChatApi chatApi;


    @PostConstruct
    private void getAllWallets() {
        GetAllWalletsResponse getAllWalletsResponse = paymentApi.getAllWallets();
        if (getAllWalletsResponse.getSuccess()) {
            walletIds = getAllWalletsResponse.getPayload().stream().map(walletSystemDTO -> walletSystemDTO.getWalletId()).collect(Collectors.toList());
            numberWallet = walletIds.size();
        } else {
            log.error("Can't get wallets, message: {}", getAllWalletsResponse.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void createLuckyMoney(CreateLuckyMoneyRequest request) {
        int currentWallet = count.getAndUpdate(c -> c < numberWallet - 1 ? c + 1 : 0);
        User user = getCurrentUser();
        long userId = user.getId();
        long walletId = walletIds.get(currentWallet);
        String sessionChatId = request.getSessionChatId();
        log.info("Send lucky money for user {} by wallet {}", user.getId(), walletId);

        long amount = transferMoneyFromUser(userId, walletId, request.getSoftToken(), request.getMessage());

        LocalDateTime createdAt = LocalDateTime.now();
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .userId(userId)
                .systemWalletId(walletId)
                .sessionChatId(sessionChatId)
                .amount(amount)
                .restMoney(amount)
                .numberBag(request.getNumberBag())
                .restBag(request.getNumberBag())
                .type(request.getType())
                .wishMessage(request.getMessage())
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        luckyMoneyRepository.save(luckyMoney);

//        sendMessageLuckyMoney(userId, sessionChatId, request.getMessage(), luckyMoney.getId() ,createdAt);


    }

    public void sendMessageLuckyMoney(long userId, String sessionChatId,String message, long luckyMoneyId, LocalDateTime createdAt ){
        CreateLuckyMoneyMessageRequest request = CreateLuckyMoneyMessageRequest.builder()
                .userId(userId)
                .sessionId(sessionChatId)
                .message(message)
                .luckyMoneyId(luckyMoneyId)
                .createdAt(createdAt.toString())
                .build();
        chatApi.createLuckyMoneyMessage(request);
    }

    private long transferMoneyFromUser(Long userId, Long walletId, String softToken, String message) {
        CreateTransferFromUserRequest request = CreateTransferFromUserRequest.builder()
                .userId(userId)
                .message(message)
                .softToken(softToken)
                .walletId(walletId)
                .build();
        CreateTransferFromUserResponse response = paymentApi.createTransferFromUser(request);
        if (!response.getSuccess()) {
            throw new CannotTransferMoneyException();
        }
        return response.getPayload().getAmount();
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
