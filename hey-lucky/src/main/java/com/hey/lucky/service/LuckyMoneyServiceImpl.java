package com.hey.lucky.service;

import com.hey.lucky.api.ChatApi;
import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;
import com.hey.lucky.dto.chat_service.CreateReceiveLuckyMoneyMessageRequest;
import com.hey.lucky.dto.payment_service.*;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.repository.LuckyMoneyRepository;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;
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

    @Override
    @Transactional
    public void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) {
        User user = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LuckyMoney luckyMoney = luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())
                .orElseThrow(() -> {
                    throw new LuckyMoneyInvalidException();
                });
        checkExpiredOfLuckyMoney(luckyMoney.getExpiredAt(),now);
        checkOutOfBag(luckyMoney.getRestBag());
        checkUserHadReceived(luckyMoney.getId(),user.getId());


        long restMoney = luckyMoney.getRestMoney();
        int restBag = luckyMoney.getRestBag();
        long amount = calculateAmountLuckyMoney(restMoney, restBag, luckyMoney.getType());

        transferMoneyToUser(luckyMoney.getSystemWalletId(), user.getId(), amount, luckyMoney.getWishMessage());

        luckyMoney.setRestMoney(restMoney - amount);
        luckyMoney.setRestBag(restBag - 1);

        luckyMoneyRepository.save(luckyMoney);

        ReceivedLuckyMoney receivedLuckyMoney = ReceivedLuckyMoney.builder()
                .luckyMoneyId(luckyMoney.getId())
                .receiverId(user.getId())
                .amount(amount)
                .createdAt(now).build();

        receivedLuckyMoneyRepository.save(receivedLuckyMoney);
//        sendMessageReceiveLuckyMoney(user.getId(),luckyMoney.getSessionChatId(),luckyMoney.getId(),amount,luckyMoney.getWishMessage(),now);

    }
    private void checkOutOfBag(int restBag){
        if (restBag == 0){
            throw new OutOfBagException();
        }
    }
    private void checkExpiredOfLuckyMoney(LocalDateTime expiredAt, LocalDateTime now){
        if (expiredAt.isBefore(now)) {
            throw new LuckyMoneyExpiredException();
        }

    }
    private void checkUserHadReceived(long luckyMoneyId, Long receiverId) {
        if (receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(luckyMoneyId,receiverId))
            throw new HadReceivedException();
    }

    private void sendMessageReceiveLuckyMoney(long receiverId, String sessionChatId, long luckeyMoneyId, long amount, String wishMessage, LocalDateTime now) {
        CreateReceiveLuckyMoneyMessageRequest request = CreateReceiveLuckyMoneyMessageRequest.builder()
                .receiverId(receiverId)
                .sessionId(sessionChatId)
                .luckyMoneyId(luckeyMoneyId)
                .amount(amount)
                .message(wishMessage)
                .receivedAt(now.toString()).build();
        chatApi.createReceiveLuckyMoneyMessage(request);
    }

    private void transferMoneyToUser(Long systemWalletId, long receiverId, long amount, String wishMessage) {
        log.info("Transfer {} to user {} by wallet {}", amount, receiverId, systemWalletId);
        CreateTransferToUserRequest request = CreateTransferToUserRequest.builder()
                .walletId(systemWalletId)
                .receiverId(receiverId)
                .amount(amount)
                .message(wishMessage)
                .build();
        CreateTransferToUserResponse response = paymentApi.createTransferToUser(request);
        if (!response.getSuccess()) {
            throw new CannotTransferMoneyException(response.getMessage());
        }
    }

    private long calculateAmountLuckyMoney(Long restMoney, int restBag, String type) {
        switch (type) {
            case TypeLuckyMoney.RANDOM:
            case TypeLuckyMoney.EQUALLY:
            default: {
                return restMoney / restBag;
            }
        }
    }

    public void sendMessageLuckyMoney(long userId, String sessionChatId, String message, long luckyMoneyId, LocalDateTime createdAt) {
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
            throw new CannotTransferMoneyException(response.getMessage());
        }
        return response.getPayload().getAmount();
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
